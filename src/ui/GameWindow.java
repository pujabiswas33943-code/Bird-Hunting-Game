package ui;
import engine.InputHandler;
import engine.SaveManager;
import engine.SoundManager;
import entity.Arrow;
import entity.Bow;
import entity.PlayerStats;
import engine.GameEngine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameWindow extends JFrame {

    private InfoPanel infoPanel;
    private GamePanel gamePanel;
    private Timer hudTimer;
    private GameEngine engine;
    private String playerName;


    public GameWindow() {
        setTitle("Bird Hunting Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1280, 720);
        addWindowListener(new java.awt.event.WindowAdapter(){

            public void windowClosing(java.awt.event.WindowEvent e){

                if(engine != null){

                    SaveManager.save(playerName, engine.getStats());
                }
                System.exit(0);
            }

        });
        showStartScreen();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showStartScreen() {
        getContentPane().removeAll();
        StartScreen startScreen = new StartScreen(this::startGame);
        add(startScreen);

        revalidate();
        repaint();


    }

    private void startGame(String name) {
        if(name == null || name.trim().isEmpty()){
            playerName = "Guest";
        }
        else {
            playerName = name;
        }

        if(SaveManager.exists(playerName)){
            PlayerStats oldStats = SaveManager.loadStats(playerName);
            SoundManager.play("src/sounds/welcome.wav");
            WelcomeDialog dialog = new WelcomeDialog(this, playerName, oldStats);
            dialog.setVisible(true);

        }
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        gamePanel = new GamePanel();

        engine = new GameEngine(gamePanel);
        gamePanel.setEngine(engine);

        gamePanel.setRestartListener(() -> {
            SoundManager.play("src/sounds/button_click.wav");
            engine.restart();
        });
        engine.start();

        SaveManager.load(playerName, engine.getStats());

        infoPanel = new InfoPanel(
                this::onArrowButton,
                e -> openShop(),
                e -> engine.restart()
        );
        boolean[] paused = {false};

        infoPanel.getPauseButton().addActionListener(e -> {

            engine.setPaused(!engine.isPaused());

            if (engine.isPaused()) {
                infoPanel.getPauseButton().setText("▶ Resume");
            } else {
                infoPanel.getPauseButton().setText("⏸ Pause");
            }

            gamePanel.requestFocusInWindow();
        });
        //Shop Listener press s
        gamePanel.setShopListener(this::openShop);


        InputHandler input = new InputHandler(new InputHandler.InputCallback() {
            @Override public void onDragStart(int mouseX, int mouseY)   { engine.dragStart(mouseX, mouseY); }
            @Override public void onDragUpdate(int mouseX, int mouseY)  { engine.dragUpdate(mouseX, mouseY); }
            @Override public void onDragRelease(int mouseX, int mouseY) { engine.dragRelease(mouseX, mouseY); }
            @Override public void onSelectArrow(Arrow.ArrowType type)   { engine.selectArrow(type); }
            @Override public void onUpgradeShop()                       { openShop(); }
            @Override public void onRestart()                           { engine.restart(); }
            @Override public void onMouseMoved(int x, int y)           { engine.aimBow(x, y); }
            @Override public void onPause() {
                infoPanel.getPauseButton().doClick();
            }
        });
        gamePanel.addKeyListener(input);
        gamePanel.addMouseListener(input);
        gamePanel.addMouseMotionListener(input);
        gamePanel.setFocusable(true);



        add(infoPanel.getTopPanel(), BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel.getBottomPanel(), BorderLayout.SOUTH);


        revalidate();
        repaint();

        hudTimer = new Timer(33, e -> {
            PlayerStats stats = engine.getStats();
            infoPanel.update(
                    stats.getScore(),
                    stats.getLives(),
                    stats.getCoins(),
                    stats.getCombo()
            );
            infoPanel.updateArrow(
                    stats.getArrowCount(Arrow.ArrowType.POWER),
                    stats.getArrowCount(Arrow.ArrowType.THUNDER),
                    stats.getArrowCount(Arrow.ArrowType.ICE),

                    stats.getArrowCount(Arrow.ArrowType.NET)

            );
            infoPanel.highlightButton(engine.getSelectedArrowIndex());
            infoPanel.updateDrawn(engine.getDrawnArrowCount());

            boolean[] unlocked = {
                    engine.isArrowUnlocked(Arrow.ArrowType.NORMAL),
                    engine.isArrowUnlocked(Arrow.ArrowType.POWER),
                    engine.isArrowUnlocked(Arrow.ArrowType.THUNDER),
                    engine.isArrowUnlocked(Arrow.ArrowType.ICE),
                    engine.isArrowUnlocked(Arrow.ArrowType.NET)
            };
            infoPanel.updateLockedButtons(unlocked);
        });
        hudTimer.start();
        gamePanel.requestFocusInWindow();
    }


    private void onArrowButton(ActionEvent e) {
        //which button pressed
        int index = Integer.parseInt(e.getActionCommand()) - 1;
        infoPanel.highlightButton(index);
        Arrow.ArrowType type = switch (e.getActionCommand()) {
            case "1" -> Arrow.ArrowType.NORMAL;
            case "2" -> Arrow.ArrowType.POWER;
            case "3" -> Arrow.ArrowType.THUNDER;
            case "4" -> Arrow.ArrowType.ICE;
            case "5" -> Arrow.ArrowType.NET;
            default -> Arrow.ArrowType.NORMAL;
        };

        engine.selectArrow(type);

        gamePanel.requestFocusInWindow();

    }

    public void openShop() {
        engine.setPaused(true);
        UpgradeShop shop = new UpgradeShop(this, engine.getStats());
        shop.setVisible(true);
        engine.setPaused(false);
        gamePanel.requestFocusInWindow();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow());  //Thread for graphics
    }
}

