import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class Node extends JPanel
{
    private static final long serialVersionUID = 1L;

    public static class NodeColors
    {
        public static final Color waterDefaultColor = new Color(0, 102, 102);
        public static final Color waterSelectedColor = new Color(0, 90, 102);
        public static final Color waterMissColor = new Color(0, 80, 102);
        public static final Color shipDefaultColor = Ship.ShipColors.defaultColor;
        public static final Color shipHitColor = Ship.ShipColors.hitColor;
        public static final Color shipSunkColor = Ship.ShipColors.sunkColor;
    }
    
    public enum NodeState { WATER_DEFAULT, WATER_SELECTED, WATER_MISS,
                            SHIP_DEFAULT, SHIP_HIT, SHIP_SUNK };

    private Board parentBoard;
    private int row, column;
    private NodeState nodeState = NodeState.WATER_DEFAULT;
    
    public Node(int r, int c, Board parentBoard)
    {
        this.row = r;
        this.column = c;
        this.parentBoard = parentBoard;
        
        this.setBackground(NodeColors.waterDefaultColor);
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                alertNodeMouseEvent(Node.this, e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                alertNodeMouseEvent(Node.this, e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                alertNodeMouseEvent(Node.this, e);
            }
        });
    }
    
    public Board getParentBoard()
    {
        return parentBoard;
    }
    
    public int getRow()
    {
        return row;
    }
    
    public int getColumn()
    {
        return column;
    }
    
    public NodeState getNodeState()
    {
        return nodeState;
    }
    
    public void setNodeState(NodeState ns)
    {
        nodeState = ns;
        switch(ns)
        {
        case WATER_DEFAULT:
            this.setBackground(NodeColors.waterDefaultColor);
            break;
        case WATER_SELECTED:
            this.setBackground(NodeColors.waterSelectedColor);
            break;
        case WATER_MISS:
            this.setBackground(NodeColors.waterMissColor);
            break;
        case SHIP_DEFAULT:
            this.setBackground(NodeColors.shipDefaultColor);
            break;
        case SHIP_HIT:
            this.setBackground(NodeColors.shipHitColor);
            break;
        case SHIP_SUNK:
            this.setBackground(NodeColors.shipSunkColor);
            break;
        default:
            break;
        }
    }
    
    public boolean isDefaultState()
    {
        return nodeState == NodeState.WATER_DEFAULT;
    }
    
    public boolean isOccupied()
    {
        return nodeState == NodeState.SHIP_DEFAULT ||
                nodeState == NodeState.SHIP_HIT ||
                nodeState == NodeState.SHIP_SUNK;
    }
    
    public boolean isHit()
    {
        return nodeState == NodeState.SHIP_HIT;
    }
    
    public boolean isMiss()
    {
        return nodeState == NodeState.WATER_MISS;
    }
    
    public boolean isSunk()
    {
        return nodeState == NodeState.SHIP_SUNK;
    }
    
    public void reset()
    {
        setNodeState(NodeState.WATER_DEFAULT);
    }
    
    private void alertNodeMouseEvent(Node n, MouseEvent e)
    {
        parentBoard.alertNodeMouseEvent(n, e);
    }
}
