package ui;

import engine.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class StartScreen extends JLayeredPane implements ActionListener {
    private Image background;
    private JButton startbtn;
    private ToggleSwitch muteToggle;
    private JLabel muteLabel;
    private JProgressBar loadingbar;
    private Timer loadingTimer;
    private int laodProgress = 0;
    private boolean loading = false;
    private java.util.function.Consumer<String> onStartCallback;
    private JTextField nameField;
    private String playerName;
    private JPanel contentPanel;

    public StartScreen(java.util.function.Consumer<String> onStartCallback) {

        this.onStartCallback = onStartCallback;

        setLayout(null);

        SoundManager.playBackgroundMusic("src/sounds/start_music.wav");

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        startbtn = new JButton("START GAME");
        startbtn.setFont(new Font("Arial", Font.BOLD, 32));
        startbtn.setForeground(Color.WHITE);
        startbtn.setBackground(new Color(220, 80, 40));
        startbtn.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 100), 2));
        startbtn.setFocusable(false);
        startbtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startbtn.addActionListener(e -> {
            SoundManager.play("src/sounds/button_click.wav");
            startLoading();
        });

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(300, 45));
        nameField.setFont(new Font("Arial", Font.ITALIC, 18));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setForeground(new Color(230, 240, 255));
        nameField.setBackground(new Color(15, 25, 45));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 220, 170), 3),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        nameField.setCaretColor(new Color(100, 220, 255));
        nameField.setText("Enter Player Name");

        loadingbar = new JProgressBar(0, 100);
        loadingbar.setPreferredSize(new Dimension(400, 25));
        loadingbar.setStringPainted(true);
        loadingbar.setString("LOADING...");
        loadingbar.setForeground(new Color(60, 200, 60));
        loadingbar.setBackground(new Color(30, 30, 30));
        loadingbar.setFont(new Font("Arial", Font.BOLD, 12));
        loadingbar.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        contentPanel.add(nameField, gbc);

        gbc.gridy = 1;
        contentPanel.add(startbtn, gbc);

        gbc.gridy = 2;
        contentPanel.add(loadingbar, gbc);

        add(contentPanel, JLayeredPane.DEFAULT_LAYER);

        muteToggle = new ToggleSwitch(!SoundManager.isMuted());   // on sound
        muteToggle.setOnToggle(() -> {
            boolean soundOn = muteToggle.isOn();
            SoundManager.setMuted(!soundOn);
            if (soundOn) {
                SoundManager.playBackgroundMusic("src/sounds/start_music.wav");
            }
        });
        add(muteToggle, JLayeredPane.PALETTE_LAYER);

        muteLabel = new JLabel("SOUND") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // ★ Semi-transparent background pill/box
                g2.setColor(new Color(0, 0, 0, 140));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // ★ subtle border
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.dispose();
                super.paintComponent(g);   // erpor normal text akbe
            }
        };
        muteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        muteLabel.setForeground(new Color(255, 255, 255));   // pure white, high contrast
        muteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        muteLabel.setOpaque(false);
        add(muteLabel, JLayeredPane.PALETTE_LAYER);

        try {
            background = new ImageIcon("src/images/start_background.jpeg").getImage();
        } catch (Exception e) {
            background = null;
        }

        loadingTimer = new Timer(30, this);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutChildren();
            }
        });
    }

    private void layoutChildren() {
        int w = getWidth();
        int h = getHeight();

        if (contentPanel != null) {
            contentPanel.setBounds(0, 0, w, h);
        }

        if (muteToggle != null && muteLabel != null) {
            int toggleW = 60, toggleH = 30;
            int margin = 20;
            muteToggle.setBounds(w - toggleW - margin, margin, toggleW, toggleH);

            int labelW = 70, labelH = 30;
            muteLabel.setBounds(w - toggleW - margin - labelW - 8, margin, labelW, labelH);
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        layoutChildren();
    }

    private void startLoading() {
        playerName = nameField.getText();

        if (playerName.trim().isEmpty() || playerName.equals("Enter Player Name")) {
            playerName = "Guest";
        }

        startbtn.setVisible(false);
        nameField.setVisible(false);
        muteToggle.setVisible(false);
        muteLabel.setVisible(false);

        loadingbar.setVisible(true);

        loading = true;
        SoundManager.stopBackgroundMusic();
        loadingTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (laodProgress < 100) {
            laodProgress += 2;
            loadingbar.setValue(laodProgress);
            loadingbar.setString("LOADING...");
            repaint();
        } else {
            loadingTimer.stop();
            onStartCallback.accept(playerName);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (background != null) {
            g2d.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
    }
}