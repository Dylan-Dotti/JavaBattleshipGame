import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class Board extends JPanel
{
    private static final long serialVersionUID = 1L;
    
    private Player owner;
    private Node[][] board;
    
    public Board(int rows, int cols, Player owner)
    {
        this.owner = owner;
        
        this.setLayout(new GridLayout(rows, cols, 1, 1));
        this.setPreferredSize(new Dimension(40 * cols, 40 * rows));
        board = new Node[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node n = new Node(r, c, this);
                this.add(n);
                board[r][c] = n; 
            }
        }
        
        this.setBackground(Color.GRAY);
    }
    
    public Node getNodeAt(int r, int c)
    {
        return board[r][c];
    }
    
    public List<Node> getAdjacentNodes(Node n)
    {
        List<Node> adjNodes = new ArrayList<>();
        if (coordinatesInBounds(n.getRow() - 1, n.getColumn())) {
            adjNodes.add(board[n.getRow() - 1][n.getColumn()]);
        }
        if (coordinatesInBounds(n.getRow(), n.getColumn() + 1)) {
            adjNodes.add(board[n.getRow()][n.getColumn() + 1]);
        }
        if (coordinatesInBounds(n.getRow() + 1, n.getColumn())) {
            adjNodes.add(board[n.getRow() + 1][n.getColumn()]);
        }
        if (coordinatesInBounds(n.getRow(), n.getColumn() - 1)) {
            adjNodes.add(board[n.getRow()][n.getColumn() - 1]);
        }
        return adjNodes;
    }
    
    public List<Node> getNodesInRow(int r)
    {
        List<Node> l = new ArrayList<>();
        for (Node n : board[r]) {
            l.add(n);
        }
        return l;
    }
    
    public List<Node> getNodesInCol(int c)
    {
        List<Node> l = new ArrayList<>();
        for (int r = 0; r < getRows(); r++) {
            l.add(board[r][c]);
        }
        return l;
    }
    
    public Player getOwner()
    {
        return owner;
    }
    
    public int getRows()
    {
        return board.length;
    }
    
    public int getCols()
    {
        return board[0].length;
    }
    
    public void moveShipTo(Ship s, Node n)
    {
        moveShipTo(s, n.getRow(), n.getColumn());
    }
    
    public void moveShipTo(Ship s, int r, int c)
    {
        List<Node> occupiedNodes = new ArrayList<Node>();
        if (s.getOrientation() == Ship.ShipOrientation.HORIZONTAL) {
            for (int i = 0; i < s.getLength(); i++) {
                Node n = board[r][c + i];
                n.setNodeState(Node.NodeState.SHIP_DEFAULT);
                occupiedNodes.add(n);
            }
        }
        else {
            for (int i = 0; i < s.getLength(); i++) {
                Node n = board[r + i][c];
                n.setNodeState(Node.NodeState.SHIP_DEFAULT);
                occupiedNodes.add(n);
            }
        }
        s.setOccupiedNodes(occupiedNodes);
    }
    
    public boolean canMoveShipTo(Ship s, Node n)
    {
        return canMoveShipTo(s, n.getRow(), n.getColumn());
    }
    
    public boolean canMoveShipTo(Ship s, int r, int c)
    {
        if (!coordinatesInBounds(r, c) || board[r][c].isMiss()) {
            return false;
        }
        if (s.getOrientation() == Ship.ShipOrientation.HORIZONTAL) {
            if (!coordinatesInBounds(r, c + s.getLength() - 1)) {
                return false;
            }
            for (int i = 0; i < s.getLength(); i++) {
                Node n = board[r][c + i];
                if (n.isOccupied()) {
                    if (s.getOccupiedNodes() == null ||
                            !s.getOccupiedNodes().contains(n)) {
                        return false;
                    }
                }
                else if (n.isMiss()) {
                    return false;
                }
            }
        }
        else if (s.getOrientation() == Ship.ShipOrientation.VERTICAL) {
            if (!coordinatesInBounds(r + s.getLength() - 1, c)) {
                return false;
            }
            for (int i = 0; i < s.getLength(); i++) {
                Node n = board[r + i][c];
                if (n.isOccupied()) {
                    if (s.getOccupiedNodes() == null ||
                            !s.getOccupiedNodes().contains(n)) {
                        return false;
                    }
                }
                else if (n.isMiss()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean coordinatesInBounds(int r, int c)
    {
        return r >= 0 && r < getRows() &&
                c >= 0 && c < getCols();
    }
    
    public void resetBoard()
    {
        for (Node[] row : board) {
            for (Node n : row) {
                n.reset();
            }
        }
    }
    
    protected void alertNodeMouseEvent(Node n, MouseEvent e)
    {
        owner.alertNodeMouseEvent(n, e);
    }
}
