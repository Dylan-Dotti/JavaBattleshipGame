import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;


public class MainMenuPanel extends JPanel
{    
    private static final long serialVersionUID = 1L;

    public MainMenuPanel(BattleshipFrame parentFrame)
    {
        JButton singlePlayer = new JButton("Single Player");
        singlePlayer.setPreferredSize(new Dimension(singlePlayer.getWidth() * 2, singlePlayer.getHeight() * 2));
        singlePlayer.setFocusable(false);
        singlePlayer.setBackground(Color.WHITE);
        JButton twoPlayer = new JButton("Two Player (WiP)");
        twoPlayer.setPreferredSize(new Dimension(twoPlayer.getWidth() * 2, twoPlayer.getHeight() * 2));
        twoPlayer.setFocusable(false);
        twoPlayer.setBackground(Color.WHITE);
        
        singlePlayer.addActionListener(e -> parentFrame.startGame(true));
        twoPlayer.addActionListener(e -> parentFrame.startGame(false));
        
        this.setLayout(new GridLayout(2, 1));
        this.setPreferredSize(new Dimension(300, 150));
        this.add(singlePlayer);
        this.add(twoPlayer);
    }
}
