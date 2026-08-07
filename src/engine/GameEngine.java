package engine;

import entity.Arrow;
import entity.Bird;
import entity.Bow;
import entity.PlayerStats;
import ui.GamePanel;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameEngine implements ActionListener {

    private static final int FPS = 60;
    private List<Bird> birds;
    private List<Arrow> arrows;
    private List<ScorePopup> scorePopups;
    private Bow bow;
    private PlayerStats stats;
    private Spawner spawner;
    private CollisionDetector collisionDetector;
    private Arrow.ArrowType selectedArrow;
    private boolean gameOver;
    private boolean paused;
    private int cloudOffset;
    private Timer gameTimer;
    private GamePanel panel;
    private int drawnArrowCount;
    private boolean bombExploded;
    private int currentLevel = 1;
    private int previousLevel = 1;
    private boolean levelTransition = false;
    private int levelTransitionTimer = 0;
    private static final int LEVEL_TRANSITION_DURATION = 240;
    private boolean lowLivesWarningShown = false;
    private boolean showLowLivesAlert = false;
    private int lowLivesAlertTimer = 0;
    private static final int LOW_LIVES_ALERT_DURATION = 240;
    public GameEngine(GamePanel panel) {
        this.panel        = panel;
        birds             = new ArrayList<>();
        arrows            = new ArrayList<>();
        scorePopups       = new ArrayList<>();
        stats             = new PlayerStats();
        spawner           = new Spawner();
        collisionDetector = new CollisionDetector();
        selectedArrow     = Arrow.ArrowType.NORMAL;
        gameOver          = false;
        paused=false;
        cloudOffset       = 0;
        bombExploded = false;

        bow = new Bow(1, 1);
        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                bow.updatePosition(panel.getWidth() - 40, panel.getHeight() - 85);

            }
        });

        gameTimer = new Timer(1000 / FPS, this);
    }

    public void start() {
        gameOver = false;
        paused=false;
        gameTimer.start(); // starting game timer
        SoundManager.playBackgroundMusic("src/sounds/background.wav");
    }

    public void restart() {
        birds.clear();
        arrows.clear();
        scorePopups.clear();
        stats.reset();
        spawner       = new Spawner();  // initiate new birds from starting level
        selectedArrow = Arrow.ArrowType.NORMAL;
        cloudOffset   = 0;
        gameOver      = false;
        paused=false;
        drawnArrowCount = 0;
        bombExploded = false;
        currentLevel = 1;
        lowLivesWarningShown = false;
        showLowLivesAlert = false;
        if (!gameTimer.isRunning()) gameTimer.start();
        SoundManager.playBackgroundMusic("src/sounds/background.wav");
    }

    public boolean isLevelTransition() {
        return levelTransition;
    }




    public static class ScorePopup {
        public String text;
        public double x, y;
        public int life;
        public ScorePopup(String text, double x, double y) {
            this.text = text;
            this.x    = x;
            this.y    = y;
            this.life = 60;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) {
            return;
        }

        if (!paused) {
            update();
        }

        panel.repaint();
    }

    private void update() {
        if (levelTransition) {
            levelTransitionTimer--;
            if (levelTransitionTimer <= 0) {
                levelTransition = false;
            }
            return;
        }
        if (showLowLivesAlert) {
            lowLivesAlertTimer--;
            if (lowLivesAlertTimer <= 0) {
                showLowLivesAlert = false;
            }
        }


        cloudOffset = (cloudOffset + 1) % Math.max(1, panel.getWidth());

        int newLevel;
        if (stats.getScore() >= 100) {
            newLevel = 3;
        } else if (stats.getScore() >= 50) {
            newLevel = 2;
        } else {
            newLevel = 1;
        }

        if (newLevel != currentLevel) {
            currentLevel = newLevel;
            levelTransition = true;
            levelTransitionTimer = LEVEL_TRANSITION_DURATION;
            SoundManager.play("src/sounds/level_up1.wav");

            stats.setLives(20);
            lowLivesWarningShown = false;
            return;
        }


        spawner.update(birds, stats.getScore(), panel.getHeight(),currentLevel);

        for (Bird bird : birds) {
            bird.update(panel.getWidth(), panel.getHeight());
        }
        for (Arrow arrow : arrows) {
            arrow.update();
        }

        collisionDetector.check(arrows, birds, panel.getWidth(),
                (arrow, bird) -> {

                    if(arrow.getType() == Arrow.ArrowType.THUNDER){
                        SoundManager.play("src/sounds/Thunder.wav");
                        bird.setElectric(true);
                        bird.hit(1);
                    }
                    else if(arrow.getType() == Arrow.ArrowType.ICE){
                        SoundManager.play("src/sounds/ice.wav");
                        bird.freezeWithIce(140);
                        bird.hit(1);

                        double freezeRadius = 150;
                        for (Bird other : birds) {
                            if (other == bird || !other.isActive()) continue;

                            double dx = other.getX() - bird.getX();
                            double dy = other.getY() - bird.getY();
                            double distance = Math.sqrt(dx * dx + dy * dy);

                            if (distance <= freezeRadius) {
                                other.freezeWithIce(120);
                                other.hit(1);

                                if (other.isDead()) {
                                    handleBirdDeath(other);
                                }
                            }
                        }
                    }
                    else if(arrow.getType() == Arrow.ArrowType.NET){
                        SoundManager.play("src/sounds/net.wav");
                        bird.freezeWithNet(150);
                        bird.setCaught(true);
                        bird.hit(1);
                    }
                    else {
                        bird.hit(1);
                    }
                    if (bird.isDead()) {
                        handleBirdDeath(bird);
                    }
                },
                bird -> {
                    if (bird.getType() != Bird.BirdType.bomb) {
                        stats.loseLife();
                        stats.resetCombo();
                        if (stats.getLives() == 5 && !lowLivesWarningShown) {
                            lowLivesWarningShown = true;
                            showLowLivesAlert = true;
                            lowLivesAlertTimer = LOW_LIVES_ALERT_DURATION;
                            SoundManager.play("src/sounds/warning.wav");
                        }
                        if (stats.isGameOver()) {
                            gameOver = true;
                            gameTimer.stop();
                            SoundManager.stopBackgroundMusic();
                            SoundManager.play("src/sounds/game_over.wav");
                        }
                    }
                }
        );

        arrows.removeIf(a -> !a.isActive());
        birds.removeIf(b -> !b.isActive() && !b.isElectric() && !b.isIced() && !b.isCaught());

        scorePopups.forEach(sp -> { sp.y -= 1.2; sp.life--; });
        scorePopups.removeIf(sp -> sp.life <= 0);
    }

    private void handleBirdDeath(Bird deadBird) {
        if (deadBird.getType() == Bird.BirdType.bomb) {
            bombExploded = true;
            gameOver = true;
            gameTimer.stop();
            SoundManager.stopBackgroundMusic();
            SoundManager.play("src/sounds/bomb_bird.wav");
            Timer gameOverSoundDelay = new Timer(2000, e -> SoundManager.play("src/sounds/game_over.wav"));
            gameOverSoundDelay.setRepeats(false);
            gameOverSoundDelay.start();

            scorePopups.add(new ScorePopup("💥 BOOM!", deadBird.getX() - 20, deadBird.getY()));
        } else {
            stats.incrementCombo();
            int multiplier = Math.min(stats.getCombo() / 3 + 1, 4);
            int finalScore = deadBird.getScoreValue() * multiplier;
            stats.addRawScore(finalScore);
            stats.addCoins(deadBird.getCoinValue());

            SoundManager.play("src/sounds/coins.wav");

            stats.incrementBirdKilled();
            stats.addCoinsEarned(deadBird.getCoinValue());

            String popupText = multiplier > 1
                    ? "+" + finalScore + " x" + multiplier + "!"
                    : "+" + finalScore;
            scorePopups.add(new ScorePopup(popupText, deadBird.getX(), deadBird.getY()));
        }
    }

    public void dragStart(int mouseX, int mouseY) {
        if (gameOver || paused) return;
        SoundManager.play("src/sounds/bowLoading.wav");
        bow.startDrag(mouseX, mouseY);
    }

    public void dragUpdate(int mouseX, int mouseY) {
        if (gameOver || paused) return;
        bow.updateDrag(mouseX, mouseY);
    }
    public void dragRelease(int mouseX, int mouseY) {
        if (gameOver || paused) return;
        if (!bow.isDragging()) return;

        double dx = mouseX - bow.getCenterX();
        double dy = mouseY - bow.getCenterY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 15) { bow.releaseDrag(); return; }
        if (!stats.useArrow(selectedArrow))
        {
            bow.releaseDrag(); return;
        }

        double angle = Math.atan2(dy, dx) + Math.PI;
        bow.releaseDrag();

        SoundManager.play("src/sounds/arrow-swish.wav");

        if (selectedArrow == Arrow.ArrowType.POWER) {
            double[] offsets = {-0.45,-0.3, -.15, 0, 0.15, 0.3,0.45};  //for more arrows value boro rakhle arrow gula choriye jabe
            for (double offset : offsets) {
                arrows.add(new Arrow(bow.getCenterX(), bow.getCenterY(),
                        bow.getCenterX() + Math.cos(angle + offset)*100,
                        bow.getCenterY() + Math.sin(angle + offset)*100, angle + offset, Arrow.ArrowType.POWER));
            }
        } else {
            arrows.add(new Arrow(
                    bow.getCenterX(), bow.getCenterY(),
                    bow.getCenterX() + Math.cos(angle)*100,
                    bow.getCenterY() + Math.sin(angle)*100,
                    angle, selectedArrow
            ));
        }
        drawnArrowCount++;
    }

    public void selectArrow(Arrow.ArrowType type) {
        if (!isArrowUnlocked(type)) {
            return;
        }
        selectedArrow = type;
    }
    public void aimBow(int x, int y) {
        bow.aimAt(x, y);
    }
    public void setPaused(boolean p){
        this.paused=p;
        if (p) {
            SoundManager.pauseBackgroundMusic();
        } else {
            SoundManager.resumeBackgroundMusic();
        }
    }
    public boolean isPaused(){
        return paused;
    }
    public List<Bird> getBirds()              { return birds; }
    public List<Arrow> getArrows()            { return arrows; }
    public List<ScorePopup> getScorePopups()  { return scorePopups; }
    public Bow getBow()                       { return bow; }
    public PlayerStats getStats()             { return stats; }
    public int getCloudOffset()               { return cloudOffset; }
    public boolean isGameOver()               { return gameOver; }
    public Arrow.ArrowType getSelectedArrow() { return selectedArrow; }
    public int getSelectedArrowIndex(){
        return selectedArrow.ordinal();
    }
    public boolean isBombExploded(){
        return bombExploded;
    }
    public  int getDrawnArrowCount(){
        return drawnArrowCount;
    }
    public int getCurrentLevel() {
        return currentLevel;
    }
    public boolean isArrowUnlocked(Arrow.ArrowType type) {
        return switch (type) {
            case NORMAL -> true;
            case POWER  -> currentLevel >= 1;
            case THUNDER, ICE, NET -> currentLevel >= 2;
        };

    }
    public int getLevelTransitionTimer() {
        return levelTransitionTimer;
    }
    public boolean isShowLowLivesAlert() {
        return showLowLivesAlert;
    }
}