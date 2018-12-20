import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameManager
{
    public enum GameState { PLACEMENT, COMBAT, END };
    
    private BattleshipFrame parentFrame;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private AI ai;
    
    private GameState gameState;
    private Node selectedNode;
    private Queue<Ship> placementShips;

    
    public GameManager(BattleshipFrame parentFrame)
    {
        this.parentFrame = parentFrame;
    }
    
    public void startGame(boolean singlePlayer)
    {
        player1 = new Player(this);
        player2 = new Player(this);
        currentPlayer = player1;
        gameState = GameState.PLACEMENT;
        ai = singlePlayer ? new AI(this, getNonCurrentPlayer()) : null;
        
        placementShips = new LinkedList<>();
        ArrayList<Map<String, Ship>> playerShips = new ArrayList<>();
        playerShips.add(player1.getShips());
        playerShips.add(player2.getShips());
        for (Map<String, Ship> ships : playerShips) {
            placementShips.add(ships.get("Carrier"));
            placementShips.add(ships.get("Battleship"));
            placementShips.add(ships.get("Cruiser"));
            placementShips.add(ships.get("Submarine"));
            placementShips.add(ships.get("Destroyer"));
        }
        
        parentFrame.changeBoardsDisplayed(null, currentPlayer.getPlayerBoard());
    }
    
    public GameState getGameState()
    {
        return gameState;
    }
    
    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }
    
    public Player getNonCurrentPlayer()
    {
        return currentPlayer == player1 ? player2 : player1;
    }
    
    protected void alertNodeMouseEvent(Node n, MouseEvent e)
    {
        switch(gameState) {
        case PLACEMENT:
            handlePlacementEvent(n, e);
            break;
        case COMBAT:
            handleCombatEvent(n, e);
            break;
        default:
            break;
        }
    }
    
    private void handlePlacementEvent(Node n, MouseEvent e)
    {
        Ship nextPlacementShip = placementShips.peek();
        Board nParentBoard = n.getParentBoard();
        
        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            if (nParentBoard.canMoveShipTo(nextPlacementShip, n)) {
                nParentBoard.moveShipTo(nextPlacementShip, n);
            }
        }
        else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            if (SwingUtilities.isLeftMouseButton(e) || 
                    (ai != null && ai.getControlledPlayer() == currentPlayer)) {
                if (nParentBoard.canMoveShipTo(nextPlacementShip, n)) {
                    nParentBoard.moveShipTo(placementShips.remove(), n);
                    if (placementShips.isEmpty()) {
                        gameState = GameState.COMBAT;
                        switchTurns();
                    }
                    else if (placementShips.peek().getOwner() 
                            != currentPlayer) {
                        switchTurns();
                    }
                }
            }
            else if (SwingUtilities.isRightMouseButton(e)) {
                nextPlacementShip.flipOrientation();
                if (nParentBoard.canMoveShipTo(nextPlacementShip, n)) {
                    nParentBoard.moveShipTo(nextPlacementShip, n);
                }
                else if (nextPlacementShip.getOccupiedNodes() != null) {
                    nextPlacementShip.flipOrientation();
                }
            }
        }
    }
    
    private void handleCombatEvent(Node n, MouseEvent e)
    {
        if (n.getParentBoard() == currentPlayer.getTargetBoard()) {
            if (e.getID() == MouseEvent.MOUSE_ENTERED) {
                setSelectedNode(n);
            }
            else if (e.getID() == MouseEvent.MOUSE_EXITED) {
                setSelectedNode(null);
            }
            else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                if (n == selectedNode && (SwingUtilities.isLeftMouseButton(e) || 
                        (ai != null && ai.getControlledPlayer() == currentPlayer))) {
                    attackNode(n);
                }
            }
        }
    }
    
    private void attackNode(Node targetNode)
    {
        Node nonCurrentPlayerNode = getNonCurrentPlayer().getPlayerBoard().
                getNodeAt(targetNode.getRow(), targetNode.getColumn());
        if (nonCurrentPlayerNode.isDefaultState()) {
            nonCurrentPlayerNode.setNodeState(Node.NodeState.WATER_MISS);
            targetNode.setNodeState(Node.NodeState.WATER_MISS);
            switchTurns();
        }
        else if (nonCurrentPlayerNode.getNodeState() == Node.NodeState.SHIP_DEFAULT) {
            for (Ship s : getNonCurrentPlayer().getShips().values()) {
                if (!s.isSunk() && s.occupiesNode(nonCurrentPlayerNode)) {
                    s.takeDamage(nonCurrentPlayerNode);
                    if (!s.isSunk()) {
                        targetNode.setNodeState(Node.NodeState.SHIP_HIT);
                    }
                    else {
                        for (Node occupiedNode : s.getOccupiedNodes()) {
                            currentPlayer.getTargetBoard().
                            getNodeAt(occupiedNode.getRow(), occupiedNode.getColumn()).
                            setNodeState(Node.NodeState.SHIP_SUNK);
                        }
                        if (getNonCurrentPlayer().allShipsSunk()) {
                            JOptionPane.showMessageDialog(parentFrame, "Player " + 
                                    (currentPlayer == player1 ? "1" : "2") + 
                                    " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                            gameState = GameState.END;
                        }
                    }
                    switchTurns();
                    return;
                }
            }
        }
    }
    
    private void switchTurns()
    {
        setSelectedNode(null);
        currentPlayer = getNonCurrentPlayer();
        if (gameState == GameState.PLACEMENT) {
            if (ai == null || currentPlayer != ai.getControlledPlayer()) {
                parentFrame.changeBoardsDisplayed(null,
                        currentPlayer.getPlayerBoard());
            }
            else {
                parentFrame.changeBoards(null, currentPlayer.getPlayerBoard());
            }
        }
        else if (gameState == GameState.COMBAT) {
            if (ai == null || currentPlayer != ai.getControlledPlayer()) {
                parentFrame.changeBoardsDisplayed(currentPlayer.getTargetBoard(),
                        currentPlayer.getPlayerBoard());
            }
            else {
                parentFrame.changeBoards(currentPlayer.getTargetBoard(),
                        currentPlayer.getPlayerBoard());
            }
        }
        
        if (ai != null && currentPlayer == ai.getControlledPlayer() && 
                gameState != GameState.END) {
            new Thread(ai).start();
        }
    }
    
    private void setSelectedNode(Node n)
    {
        if (selectedNode != null && selectedNode.getNodeState() == 
                Node.NodeState.WATER_SELECTED) {
            selectedNode.setNodeState(Node.NodeState.WATER_DEFAULT);
        }
        selectedNode = n;
        if (selectedNode != null && selectedNode.isDefaultState()) {
            selectedNode.setNodeState(Node.NodeState.WATER_SELECTED);
        }
    }
}

