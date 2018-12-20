import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Player
{
    private GameManager manager;
    private Board targetBoard;
    private Board playerBoard;
    private Map<String, Ship> ships;
    
    public Player(GameManager manager)
    {
        this.manager = manager;
        targetBoard = new Board(10, 10, this);
        playerBoard = new Board(10, 10, this);
        
        ships = new HashMap<>();
        ships.put("Destroyer", new Ship(2, this));
        ships.put("Submarine", new Ship(3, this));
        ships.put("Cruiser", new Ship(3, this));
        ships.put("Battleship", new Ship(4, this));
        ships.put("Carrier", new Ship(5, this));
    }
    
    public GameManager getManager()
    {
        return manager;
    }
    
    public Board getTargetBoard()
    {
        return targetBoard;
    }
    
    public Board getPlayerBoard()
    {
        return playerBoard;
    }
    
    public Map<String, Ship> getShips()
    {
        return ships;
    }
    
    public List<Ship> getUnsunkShips()
    {
        return ships.values().stream().filter(s -> !s.isSunk())
                .collect(Collectors.toList());
    }
    
    public boolean allShipsSunk()
    {
        for (Ship s : ships.values()) {
            if (!s.isSunk()) {
                return false;
            }
        }
        return true;
    }
    
    public Ship getShipFromNode(Node n)
    {
        for (Ship s : ships.values()) {
            if (s.occupiesNode(n)) {
                return s;
            }
        }
        return null;
    }
    
    protected void alertNodeMouseEvent(Node n, MouseEvent e)
    {
        manager.alertNodeMouseEvent(n, e);
    }
}
