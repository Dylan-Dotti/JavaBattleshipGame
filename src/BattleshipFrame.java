//import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class BattleshipFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    
    private GameManager manager;
    private Board topBoard;
    private Board bottomBoard;
    
    private BattleshipFrame()
    {
        super("Battleship");
        manager = new GameManager(this);
        //MainMenuPanel menuPanel = new MainMenuPanel(this);
        
        JMenuBar mb = new JMenuBar();
        this.setJMenuBar(mb);
        
        JMenu program = new JMenu("Game");
        JMenuItem reset = new JMenuItem("New");
        reset.setMnemonic(KeyEvent.VK_N);
        reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        reset.addActionListener(e -> {
            manager = new GameManager(BattleshipFrame.this);
            manager.startGame(true);
            /*
            changeBoardsDisplayed(null, null);
            BattleshipFrame.this.setLayout(new BorderLayout());
            BattleshipFrame.this.add(menuPanel, BorderLayout.CENTER);
            BattleshipFrame.this.pack();
            BattleshipFrame.this.setLocationRelativeTo(null);
            BattleshipFrameFrame.this.repaint();
            */
        });
        JMenuItem quit = new JMenuItem("Quit");
        quit.setMnemonic(KeyEvent.VK_Q);
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        quit.addActionListener(e -> BattleshipFrame.this.dispose());
        
        program.add(reset);
        program.addSeparator();
        program.add(quit);
        mb.add(program);
        
        //this.setLayout(new BorderLayout());
        //this.add(menuPanel, BorderLayout.CENTER);
        
        this.getContentPane().setBackground(Color.BLACK);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
        
        startGame(true);
    }
    
    public void startGame(boolean singlePlayer)
    {
        changeBoardsDisplayed(null, null);
        manager.startGame(singlePlayer);
    }
    
    public void changeBoards(Board top, Board bottom)
    {
        topBoard = top;
        bottomBoard = bottom;
    }
    
    public void changeBoardsDisplayed(Board top, Board bottom)
    {
        this.getContentPane().removeAll();
        
        if (top == null || bottom == null) {
            this.setLayout(new GridLayout(1, 1, 0, 10));
        }
        else {
            this.setLayout(new GridLayout(2, 1, 0, 10));
        }
        
        changeBoards(top, bottom);
        
        if (topBoard != null) {
            this.add(topBoard);
        }
        if (bottomBoard != null) {
            this.add(bottomBoard);
        }
        this.pack();
        this.setLocationRelativeTo(null);
        this.repaint();
    }
    
    public Board getTopBoard()
    {
        return topBoard;
    }
    
    public Board getBottomBoard()
    {
        return bottomBoard;
    }
    
    public static void main(String[] args)
    {
        new BattleshipFrame();
    }
}
