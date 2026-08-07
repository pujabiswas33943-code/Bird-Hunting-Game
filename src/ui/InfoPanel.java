package ui;
import engine.SoundManager;
import entity.Arrow;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import entity.PlayerStats;



public class InfoPanel {

    private JPanel topPanel;
    private JPanel bottomPanel;
    private ImageIcon lockIcon;
    private ImageIcon arrowIcon;


    private JLabel scoreLabel,livesLabel,coinLabel,comboLabel;
    private JLabel powerLabel,thunderLabel,iceLabel, netLabel,drawLabel;
    private JComponent keysLabel;

    private JButton[] arrowButtons;
    private JButton pauseBtn;
    public InfoPanel(ActionListener arrowButtonListener,ActionListener shopListener, ActionListener restartListener){
        buildTopPanel(arrowButtonListener, shopListener, restartListener);
        buildBottomPanel();
    }


    private void buildTopPanel(ActionListener arrowL, ActionListener shopL, ActionListener restartL){
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(25, 25, 50),
                        getWidth(), 0, new Color(15, 15, 35)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

                g2.dispose();
            }
        };
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(860, 36));

        try {
            Image img = new ImageIcon("src/images/lock.png").getImage();
            Image scaledImg = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            lockIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            lockIcon = null;
        }
        try {
            Image img = new ImageIcon("src/images/arrowicon.png").getImage();
            Image scaledImg = img.getScaledInstance(19, 19, Image.SCALE_SMOOTH);
            arrowIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            arrowIcon = null;
        }

        scoreLabel = statBadge("\uD83C\uDFC6", "Score: 0", new Color(255, 215, 60));
        livesLabel = statBadge("\u2764", "Lives: 12", new Color(255, 90, 90));
        coinLabel  = statBadge("\uD83E\uDE99", "Coin: 150", new Color(255, 200, 80));
        comboLabel = statBadge("\uD83D\uDD25", "Combo: 0", new Color(255, 140, 60));

        topPanel.add(scoreLabel);
        topPanel.add(livesLabel);
        topPanel.add(coinLabel);
        topPanel.add(comboLabel);


        topPanel.add(Box.createHorizontalStrut(20));
        JLabel arrowTag = infoLabel("Arrow:");
        arrowTag.setForeground(new Color(180, 190, 255));
        topPanel.add(arrowTag);

        String[] labels={"Normal" , "Power", "Thunder", "Ice", "Net"};
        arrowButtons=new JButton[5];
        for(int i=0; i<5; i++){
            arrowButtons[i] = arrowButton(labels[i], String.valueOf(i + 1));
            arrowButtons[i].setIcon(arrowIcon);
            arrowButtons[i].setHorizontalTextPosition(SwingConstants.RIGHT);
            arrowButtons[i].setIconTextGap(6);
            arrowButtons[i].addActionListener(e -> SoundManager.play("src/sounds/button_click.wav"));
            arrowButtons[i].addActionListener(arrowL);
            topPanel.add(arrowButtons[i]);
        }

        topPanel.add(Box.createHorizontalStrut(10));

        JButton shopbtn = arrowButton("Upgrade Shop", "shop");
        shopbtn.setBackground(new Color(255, 210, 90));
        shopbtn.addActionListener(e -> {
            SoundManager.play("src/sounds/button_click.wav");
            shopL.actionPerformed(e);
        });
        topPanel.add(shopbtn);

        JButton restartbtn = arrowButton("Restart", "restart");
        restartbtn.setBackground(new Color(255, 140, 140));
        restartbtn.addActionListener(e -> SoundManager.play("src/sounds/button_click.wav"));
        restartbtn.addActionListener(restartL);
        topPanel.add(restartbtn);

        pauseBtn = arrowButton("|| Pause", "pause");
        pauseBtn.setBackground(new Color(150, 200, 255));
        pauseBtn.addActionListener(e -> SoundManager.play("src/sounds/button_click.wav"));
        topPanel.add(pauseBtn);
    }

    private void buildBottomPanel() {
        bottomPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20, 60, 30),
                        0, getHeight(), new Color(35, 100, 45)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawLine(0, 0, getWidth(), 0);

                g2.dispose();
            }
        };
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(860, 40));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));

        keysLabel = buildKeysLabel();

        powerLabel   = ammoBadge("\u26A1", "Power", 0, new Color(255, 180, 60));
        thunderLabel = ammoBadge("\uD83C\uDF29", "Thunder", 30, new Color(255, 230, 60));
        iceLabel     = ammoBadge("\u2744", "Ice", 30, new Color(120, 210, 255));
        netLabel     = ammoBadge("\uD83D\uDD78", "Net", 20, new Color(200, 200, 200));
        drawLabel    = ammoBadge("\uD83C\uDFF9", "Drawn", 0, new Color(255, 255, 255));

        JPanel ammoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        ammoPanel.setOpaque(false);
        ammoPanel.add(powerLabel);
        ammoPanel.add(thunderLabel);
        ammoPanel.add(iceLabel);
        ammoPanel.add(netLabel);
        ammoPanel.add(drawLabel);

        JPanel keysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
        keysPanel.setOpaque(false);
        keysPanel.add(keysLabel);

        bottomPanel.add(keysPanel, BorderLayout.WEST);
        bottomPanel.add(ammoPanel, BorderLayout.EAST);
    }


    private JLabel statBadge(String icon, String text, Color accentColor) {
        JLabel label = new JLabel(icon + " " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 120));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setForeground(accentColor);
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        label.setOpaque(false);
        return label;
    }


    private JLabel ammoBadge(String icon, String name, int count, Color accentColor) {
        JLabel label = new JLabel(icon + " " + name + ": " + count) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 100));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        label.setOpaque(false);
        return label;
    }

    private JComponent buildKeysLabel() {

        Color normalColor  = new Color(230, 233, 245);
        Color powerColor   = new Color(255, 158, 66);
        Color thunderColor = new Color(255, 213, 79);
        Color iceColor     = new Color(92, 214, 255);
        Color netColor     = new Color(182, 197, 214);
        Color shopColor    = new Color(255, 199, 74);
        Color restartColor = new Color(255, 110, 110);
        Color pauseColor   = new Color(120, 190, 255);
        Color iconColor    = new Color(140, 255, 200);
        Color sepColor     = new Color(95, 110, 135);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(20, 35, 55, 170),
                        getWidth(), 0, new Color(35, 20, 55, 170)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(140, 255, 200, 120));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        panel.add(keySegment("\u2328", iconColor));
        panel.add(keySegment(" 1=", normalColor));
        panel.add(keySegment("Normal", normalColor));
        panel.add(keySegment(sepDot(), sepColor));
        panel.add(keySegment("2=", powerColor));
        panel.add(keySegment("Power", powerColor));
        panel.add(keySegment(sepDot(), sepColor));
        panel.add(keySegment("3=", thunderColor));
        panel.add(keySegment("Thunder", thunderColor));
        panel.add(keySegment(sepDot(), sepColor));
        panel.add(keySegment("4=", iceColor));
        panel.add(keySegment("Ice", iceColor));
        panel.add(keySegment(sepDot(), sepColor));
        panel.add(keySegment("5=", netColor));
        panel.add(keySegment("Net", netColor));
        panel.add(keySegment(sepDot(), sepColor));
        panel.add(keySegment("S ", shopColor));
        panel.add(keySegment("Shop", shopColor));
        panel.add(keySegment(sepDot(), sepColor));
        panel.add(keySegment("R ", restartColor));
        panel.add(keySegment("Restart", restartColor));
        panel.add(keySegment(sepDot(), sepColor));
        panel.add(keySegment("P ", pauseColor));
        panel.add(keySegment("Pause", pauseColor));

        return panel;
    }


    private String sepDot() {
        return "   •   ";
    }


    private JLabel keySegment(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Consolas", Font.BOLD, 15));
        label.setOpaque(false);
        return label;
    }

    private JLabel infoLabel(String text) {
        JLabel l=new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Consolas",Font.BOLD,12));
        return l;
    }


    private  JButton arrowButton(String text,String cmd) {
        JButton btn = new JButton(text);
        btn.setActionCommand(cmd);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setForeground(Color.BLACK);
        btn.setBackground(new Color(200, 200, 200));
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120)));
        btn.setFocusable(false);
        btn.setPreferredSize(new Dimension(text.length() > 7 ? 110 : 80, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void update(int score, int lives, int coins, int combo) {
        scoreLabel.setText("\uD83C\uDFC6 Score: " + score);
        livesLabel.setText("\u2764 Lives: " + lives);
        coinLabel.setText("\uD83E\uDE99 Coin: " + coins);
        comboLabel.setText("\uD83D\uDD25 Combo: " + combo);


        if (lives <= 5) {
            livesLabel.setForeground(new Color(255, 40, 40));
        } else {
            livesLabel.setForeground(new Color(255, 90, 90));
        }
    }

    public void updateArrow(int power,int thunder,int ice,int net){
        updateAmmoText(powerLabel, "\u26A1 Power", power);
        updateAmmoText(thunderLabel, "\uD83C\uDF29 Thunder", thunder);
        updateAmmoText(iceLabel, "\u2744 Ice", ice);
        updateAmmoText(netLabel, "\uD83D\uDD78 Net", net);
    }

    private void updateAmmoText(JLabel label, String prefix, int count) {
        label.setText(prefix + ": " + count);
        if (count <= 5) {
            label.setForeground(new Color(255, 90, 90));
        } else {
            label.setForeground(Color.WHITE);
        }
    }

    public JPanel getTopPanel() { return topPanel;
    }

    public JPanel getBottomPanel() { return bottomPanel;
    }

    public void highlightButton(int index) {
        for (int i = 0; i < arrowButtons.length; i++) {
            arrowButtons[i].setBackground(
                    i == index
                            ? new Color(60, 120, 200)  // selected — blue
                            : new Color(200, 200, 200)    // unselected — dark
            );
        }
    }

    public void updateLockedButtons(boolean[] unlocked){
        String[] labels = {"Normal", "Power", "Thunder", "Ice", "Net"};
        for(int i = 0; i<arrowButtons.length; i++){
            arrowButtons[i].setHorizontalTextPosition((SwingConstants.RIGHT));
            arrowButtons[i].setIconTextGap(6);
            if(unlocked[i]){
                arrowButtons[i].setText(labels[i]);
                arrowButtons[i].setIcon(arrowIcon);
                arrowButtons[i].setEnabled(true);
            }else{
                arrowButtons[i].setText(labels[i]);
                arrowButtons[i].setIcon(lockIcon);
                arrowButtons[i].setDisabledIcon(lockIcon);
                arrowButtons[i].setForeground(Color.BLACK);
                arrowButtons[i].setHorizontalTextPosition(SwingConstants.LEFT);
                arrowButtons[i].setEnabled(false);
            }
        }
    }
    public void updateDrawn(int count){
        drawLabel.setText("\uD83C\uDFF9 Drawn: "+count);
    }

    public JButton getPauseButton() {
        return pauseBtn;
    }
}