package ui;

import engine.SoundManager;
import entity.PlayerStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WelcomeDialog extends JDialog {

    public WelcomeDialog(JFrame parent, String playerName, PlayerStats stats) {

        super(parent, true);

        // Transparent Dialog
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel main = new JPanel(new BorderLayout(20, 20)) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 35, 35);

                // Glass Background
                g2.setColor(new Color(18, 18, 40, 170));
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 35, 35);

                // Golden Border
                g2.setStroke(new BasicStroke(3));
                g2.setColor(new Color(255, 210, 60));
                g2.drawRoundRect(0, 0, getWidth() - 9, getHeight() - 9, 35, 35);

                g2.dispose();
            }
        };

        main.setOpaque(false);
        main.setBorder(new EmptyBorder(25, 25, 25, 25));
// Title

        JLabel title = new JLabel("WELCOME BACK!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(255, 215, 0));

        main.add(title, BorderLayout.NORTH);

        //Center

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(playerName);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setFont(new Font("Segoe UI", Font.BOLD, 34));
        name.setForeground(new Color(255, 235, 120));

        JLabel score = new JLabel("🏆 High Score : " + stats.getHighScore());
        score.setAlignmentX(Component.CENTER_ALIGNMENT);
        score.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        score.setForeground(Color.WHITE);

        JLabel coins = new JLabel("🪙 Coins : " + stats.getCoins());
        coins.setAlignmentX(Component.CENTER_ALIGNMENT);
        coins.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        coins.setForeground(new Color(120, 255, 120));

        center.add(Box.createVerticalStrut(10));
        center.add(name);
        center.add(Box.createVerticalStrut(25));
        center.add(score);
        center.add(Box.createVerticalStrut(12));
        center.add(coins);

        main.add(center, BorderLayout.CENTER);



        JButton btn = new JButton("CONTINUE");
        btn.setPreferredSize(new Dimension(230, 50));
        btn.setBackground(new Color(46, 204, 64));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e ->
        { SoundManager.play("src/sounds/button_click.wav");
            dispose();
        });

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.add(btn);

        main.add(south, BorderLayout.SOUTH);

        setContentPane(main);

        setSize(500, 340);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
}
