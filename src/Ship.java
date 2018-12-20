import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ship
{    
    public static class ShipColors
    {
        public static final Color defaultColor = Color.DARK_GRAY;
        public static final Color hitColor = new Color(150, 0, 0);
        public static final Color sunkColor = new Color(40, 40, 40);
    }
    
    public enum ShipOrientation { HORIZONTAL, VERTICAL };
    
    private Player owner;
    private Node baseNode;
    private ArrayList<Node> tailNodes;
    private int length;
    private ShipOrientation orientation;
    
    public Ship(int length, Player owner)
    {
        this.owner = owner;
        this.length = length;
        this.orientation = ShipOrientation.HORIZONTAL;
        tailNodes = new ArrayList<Node>();
    }
    
    public void takeDamage(Node n) throws IllegalArgumentException
    {
        if (baseNode != n && !tailNodes.contains(n)) {
            throw new IllegalArgumentException
            ("Ship does not occupy given node");
        }
        n.setNodeState(Node.NodeState.SHIP_HIT);
        if (allNodesHit()) {
            sinkShip();
        }
    }
    
    public void sinkShip()
    {
        if (baseNode != null) {
            baseNode.setNodeState(Node.NodeState.SHIP_SUNK);
        }
        for (Node n : tailNodes) {
            n.setNodeState(Node.NodeState.SHIP_SUNK);
        }
    }
    
    public boolean isSunk()
    {
        for (Node n : tailNodes) {
            if (n.getNodeState() != Node.NodeState.SHIP_SUNK) {
                return false;
            }
        }
        return baseNode != null && 
                baseNode.getNodeState() == Node.NodeState.SHIP_SUNK;
    }
    
    public boolean isPlaced()
    {
        return baseNode != null && !tailNodes.isEmpty();
    }
    
    public Player getOwner()
    {
        return owner;
    }
    
    public int getLength()
    {
        return length;
    }
    
    public int getBaseRow()
    {
        return baseNode.getRow();
    }
    
    public int getEndRow()
    {
        return baseNode.getRow() + 
                (orientation == ShipOrientation.VERTICAL ? length - 1 : 0);
    }
    
    public int getBaseColumn()
    {
        return baseNode.getColumn();
    }
    
    public int getEndColumn()
    {
        return baseNode.getColumn() + 
                (orientation == ShipOrientation.HORIZONTAL ? length - 1 : 0);
    }
    
    public ShipOrientation getOrientation()
    {
        return orientation;
    }
    
    public void flipOrientation()
    {
        orientation = orientation == ShipOrientation.HORIZONTAL ? 
                ShipOrientation.VERTICAL : ShipOrientation.HORIZONTAL;
    }
    
    public void setOrientation(ShipOrientation o)
    {
        orientation = o;
    }
    
    public boolean occupiesCoordinates(int r, int c)
    {
        if (baseNode == null) {
            return false;
        }
        if (baseNode.getRow() == r && baseNode.getColumn() == c) {
            return true;
        }
        for (Node n : tailNodes) {
            if (n.getRow() == r && n.getColumn() == c) {
                return true;
            }
        }
        return false;
    }
    
    public boolean occupiesNode(Node n)
    {
        return baseNode == n || tailNodes.contains(n);
    }
    
    public List<Node> getOccupiedNodes()
    {
        List<Node> l = new ArrayList<Node>();
        if (baseNode != null && tailNodes != null) {
            l.add(baseNode);
            l.addAll(tailNodes);
        }
        return l;
    }
    
    public void setOccupiedNodes(Node[] newNodes)
            throws IllegalArgumentException
    {
        if (newNodes == null) {
            setOccupiedNodes(new ArrayList<Node>());
        }
        else {
            setOccupiedNodes(Arrays.asList(newNodes));
        }
    }
    
    public void setOccupiedNodes(List<Node> newNodes) 
            throws IllegalArgumentException
    {
        clearOccupiedNodes();
        if (newNodes != null) {
            if (newNodes.size() != getLength()) {
                throw new IllegalArgumentException("Length of newNodes must "
                        + "be equal to length of ship: [E:" + getLength()
                        + " A:" + newNodes.size() + "]");
            }
            if (newNodes.size() != 0) {
                baseNode = newNodes.remove(0);
                baseNode.setNodeState(Node.NodeState.SHIP_DEFAULT);
                for (Node n : newNodes) {
                    tailNodes.add(n);
                    n.setNodeState(Node.NodeState.SHIP_DEFAULT);
                }
            }
        }
    }
    
    public void clearOccupiedNodes()
    {
        if (baseNode != null) {
            baseNode.reset();
            baseNode = null;
        }
        for (Node n : tailNodes) {
            if (n != null) {
                n.reset();
            }
        }
        tailNodes.clear();
    }
    
    private boolean allNodesHit()
    {
        for (Node n : tailNodes) {
            if (n.getNodeState() != Node.NodeState.SHIP_HIT) {
                return false;
            }
        }
        return baseNode.getNodeState() == Node.NodeState.SHIP_HIT;
    }
}
