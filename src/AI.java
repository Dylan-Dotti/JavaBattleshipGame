import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class AI implements Runnable
{
    private enum AttackPattern { NONE, NORMAL, REVERSED };
    
    private GameManager manager;
    private Player controlledPlayer;
    
    private AttackPattern attackPattern = AttackPattern.NONE;
    private Queue<ArrayList<Node>> combatLogs;
    
    
    public AI(GameManager manager, Player controlledPlayer)
    {
        this.manager = manager;
        this.controlledPlayer = controlledPlayer;
        combatLogs = new LinkedList<>();
    }
    
    @Override
    public void run()
    {
        makeMove();
    }
    
    public void makeMove()
    {
        while (controlledPlayer == manager.getCurrentPlayer()) {
            switch (manager.getGameState()) {
            case PLACEMENT:
                placeShips();
                break;
            case COMBAT:
                if (combatLogs.isEmpty()) {
                    if (manager.getNonCurrentPlayer().getUnsunkShips().size() > 4) {
                        attemptHit();
                    }
                    else {
                        attemptHitIntelligent();
                    }
                }
                else {
                    attemptRepeatHit();
                }
                break;
            default:
                break;
            }
        }
    }
    
    public Player getControlledPlayer()
    {
        return controlledPlayer;
    }
    
    private void placeShips()
    {
        do {
            Node targetNode = getRandomPlacementNode();
            if (new Random().nextBoolean()) {
                for (Ship s : controlledPlayer.getShips().values()) {
                    if (s.getOccupiedNodes().isEmpty()) {
                        s.flipOrientation();
                    }
                }
            }
            clickNode(targetNode);
        }
        while (controlledPlayer == manager.getCurrentPlayer());
    }
    
    private void attemptHit()
    {
        Node targetNode = getRandomCombatNode();
        clickNode(targetNode);
        if (targetNode.isHit()) {
            ArrayList<Node> newLog = new ArrayList<>();
            newLog.add(targetNode);
            combatLogs.add(newLog);
        }
    }
    
    private void attemptHitIntelligent()
    {
        Board targetBoard = controlledPlayer.getTargetBoard();
        int[][] weightBoard = new int[targetBoard.getRows()][targetBoard.getCols()];
        
        for (int r = 0; r < targetBoard.getRows(); r++) {
            for (int c = 0; c < targetBoard.getCols(); c++) {
                for (Ship s : manager.getNonCurrentPlayer().getUnsunkShips()) {
                    Ship s_temp = new Ship(s.getLength(), null);
                    for (int i = 0; i < s_temp.getLength(); i++) {
                        if (targetBoard.canMoveShipTo(s_temp, r, c - i)) {
                            weightBoard[r][c]++;
                        }
                    }
                    s_temp.flipOrientation();
                    for (int i = 0; i < s_temp.getLength(); i++) {
                        if (targetBoard.canMoveShipTo(s_temp, r - i, c)) {
                            weightBoard[r][c]++;
                        }
                    }
                }
            }
        }
        int maxWeight = 0;
        for (int[] row : weightBoard) {
            for (int weight : row) {
                if (weight > maxWeight) {
                    maxWeight = weight;
                }
            }
        }
        ArrayList<Node> targetNodes = new ArrayList<>();
        for (int r = 0; r < targetBoard.getRows(); r++) {
            for (int c = 0; c < targetBoard.getCols(); c++) {
                if (weightBoard[r][c] == maxWeight) {
                    targetNodes.add(targetBoard.getNodeAt(r, c));
                }
            }
        }
        Node target = targetNodes.get(new Random().nextInt(targetNodes.size()));
        clickNode(target);
        if (target.isHit()) {
            ArrayList<Node> newLog = new ArrayList<>();
            newLog.add(target);
            combatLogs.add(newLog);
        }
    }
    
    private void attemptRepeatHit()
    {
        Node targetNode = null;
        
        if (attackPattern == AttackPattern.NONE) {
            targetNode = getRandomAdjacentNode(getNewestHitNode());
            clickNode(targetNode);
            if (targetNode.isHit()) {
                attackPattern = AttackPattern.NORMAL;
            }
        }
        else if (attackPattern == AttackPattern.NORMAL) {
            targetNode = getNextNodeInPattern(getPreviousHitNode(), getNewestHitNode());
            if (targetNode != null) {
                clickNode(targetNode);
            }
            else {
                attackPattern = AttackPattern.REVERSED;
                Collections.reverse(combatLogs.peek());
            }
        }
        
        if (attackPattern == AttackPattern.REVERSED) {
            targetNode = getNextNodeInPattern(getPreviousHitNode(), getNewestHitNode());
            if (targetNode != null) {
                clickNode(targetNode);
            }
            else {
                splinterCurrentCombatLog();
                targetNode = getRandomAdjacentNode(getNewestHitNode());
                clickNode(targetNode);
                attackPattern = targetNode.isMiss() ? AttackPattern.NONE : AttackPattern.NORMAL;
            }
        }
        
        if (targetNode.isHit()) {
            combatLogs.peek().add(targetNode);
        }
        else if (targetNode.isSunk()) {
            cleanupCombatLog();
        }
    }
    
    private Node getNewestHitNode()
    {
        return combatLogs.peek().get(combatLogs.peek().size() - 1);
    }
    
    private Node getPreviousHitNode()
    {
        return combatLogs.peek().get(combatLogs.peek().size() - 2);
    }
    
    private void splinterCurrentCombatLog()
    {
        for (Node n : combatLogs.peek()) {
            ArrayList<Node> l = new ArrayList<>();
            l.add(n);
            combatLogs.add(l);
        }
        combatLogs.remove();
    }
    
    private void cleanupCombatLog()
    {
        ArrayList<Node> tempLog = new ArrayList<>();
        tempLog.addAll(combatLogs.peek());
        for (Node n : tempLog) {
            if (n.getNodeState() == Node.NodeState.SHIP_SUNK) {
                combatLogs.peek().remove(n);
            }
        }
        if (combatLogs.peek().isEmpty()) {
            combatLogs.remove();
            attackPattern = AttackPattern.NONE;
        }
        else if (combatLogs.peek().size() == 1) {
            attackPattern = AttackPattern.NONE;
        }
    }
    
    private Node getRandomPlacementNode()
    {
        Random rand = new Random();
        Board b = controlledPlayer.getPlayerBoard();
        int row = rand.nextInt(b.getRows());
        int col = rand.nextInt(b.getCols());
        return b.getNodeAt(row, col);
    }
    
    private Node getRandomCombatNode()
    {
        Random rand = new Random();
        Board b = controlledPlayer.getTargetBoard();
        while (true) {
            int row = rand.nextInt(b.getRows());
            int col = rand.nextInt(b.getCols());
            Node n = b.getNodeAt(row, col);
            if (n.isDefaultState()) {
                return n;
            }
        }
    }
    
    private Node getRandomAdjacentNode(Node n)
    {
        Board b = n.getParentBoard();
        List<Node> adjacentNodes = b.getAdjacentNodes(n);
        for (Node adjN : b.getAdjacentNodes(n)) {
            if (!adjN.isDefaultState()) {
                adjacentNodes.remove(adjN);
            }
        }
        if (adjacentNodes.isEmpty()) {
            return null;
        }
        return adjacentNodes.get(new Random().nextInt(adjacentNodes.size()));
    }
    
    private Node getNextNodeInPattern(Node posNode, Node dirNode)
    {
        Board b = posNode.getParentBoard();
        int row = dirNode.getRow() + (dirNode.getRow() - posNode.getRow());
        int col = dirNode.getColumn() + (dirNode.getColumn() - posNode.getColumn());
        if (b.coordinatesInBounds(row, col)) {
            Node n = b.getNodeAt(row, col);
            if (n.isDefaultState()) {
                return n;
            }
        }
        return null;
    }
    
    private void clickNode(Node n)
    {
        manager.alertNodeMouseEvent(n, new MouseEvent(n, MouseEvent.MOUSE_ENTERED,
                1, MouseEvent.BUTTON1, 1, 1, 1, false));
        manager.alertNodeMouseEvent(n, new MouseEvent(n, MouseEvent.MOUSE_RELEASED,
                1, MouseEvent.BUTTON1, 1, 1, 1, false));
    }
}
