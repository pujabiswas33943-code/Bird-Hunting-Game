package ui;


import entity.Bird;
import entity.Arrow;

import entity.PlayerStats;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics2D;


import engine.GameEngine;
import engine.InputHandler;
public class GamePanel extends JPanel {
    private Image forestBg;
    private Image snowBg;
    private Image level3Bg;
    private GameEngine engine;
    private Runnable shopListener;
    private JButton restartBtn;
    public GamePanel() {

        setPreferredSize(new Dimension(900, 530));
        try {
            forestBg  = new ImageIcon("src/images/background3.jpg").getImage();
            snowBg    = new ImageIcon("src/images/snowbg.jpg").getImage();
            level3Bg = new ImageIcon("src/images/level4.png").getImage();
        } catch (Exception e) {
            forestBg=snowBg=level3Bg = null;
        }
        restartBtn = new JButton(" RESTART");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 18));
        restartBtn.setForeground(Color.WHITE);
        restartBtn.setBackground(new Color(220, 60, 60));
        restartBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 120, 120), 2));
        restartBtn.setFocusable(false);
        restartBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartBtn.setVisible(false);
        add(restartBtn);
    }
        public void setEngine(GameEngine engine) {
            this.engine=engine;
        }

        public void setShopListener(Runnable listener) {
            shopListener=listener;
        }
        public PlayerStats getStats() {
            return engine.getStats();
        }
        public GameEngine getEngine(){
            return engine;
        }



        protected void paintComponent (Graphics g){
            super.paintComponent(g);
            if(engine == null)
                return;
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Image currentBg = getBackgroundForLevel(engine.getCurrentLevel());
            if (currentBg != null) {
                g2d.drawImage(currentBg, 0, 0, getWidth(), getHeight(), null);
            }

            drawClouds(g2d);  //drawing cloud

            for (Bird bird : engine.getBirds()) {
                bird.draw(g2d);
            }
            for (Arrow arrow : engine.getArrows()) {
                arrow.draw(g2d);
            }
            engine.getBow().draw(g2d);

            drawScorePopups(g2d);

            if(engine.isLevelTransition()){
                drawLevelUpOverlay(g2d,engine.getCurrentLevel());
            }
            if (engine.isShowLowLivesAlert()) {
                drawLowLivesAlert(g2d);
            }
            if (engine.isGameOver()) {
                drawGameOver(g2d);
                positionRestartButton();
                restartBtn.setVisible(true);
            } else {
                restartBtn.setVisible(false);
            }
            if(engine.isPaused()){
                drawPauseScreen(g2d);
            }
        }
    private void drawPauseScreen(Graphics2D g2d){

        g2d.setColor(new Color(0,0,0,160));
        g2d.fillRect(0,0,getWidth(),getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial",Font.BOLD,60));

        String text = "PAUSED";

        FontMetrics fm = g2d.getFontMetrics();

        int x = (getWidth()-fm.stringWidth(text))/2;
        int y = getHeight()/2;

        g2d.drawString(text,x,y);

        g2d.setFont(new Font("Arial",Font.PLAIN,22));
        g2d.drawString("Press Resume to continue",640,y+40);
    }

    private void drawLevelUpOverlay(Graphics2D g2d, int level) {

        int timer = engine.getLevelTransitionTimer();
        int total = 240;
        float progress = 1f - (timer / (float) total);
        progress = Math.max(0f, Math.min(1f, progress));   //stay between range
        float fadeAlpha = Math.max(0f, Math.min(1f, progress * 4f));   //prothom 25% shomoy e fade-in

        // dark overlay with fade
        g2d.setColor(new Color(0, 0, 0, (int)(160 * fadeAlpha)));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // level onujayi theme color
        Color themeColor = switch (level) {
            case 2 -> new Color(100, 200, 255);   // ice-blue
            case 3 -> new Color(255, 180, 40);    // golden
            default -> new Color(255, 215, 0);
        };

        int boxWidth = 480;
        int boxHeight = 230;
        int x = getWidth() / 2 - boxWidth / 2;
        int y = getHeight() / 2 - boxHeight / 2;

        // ★ slight scale-up pop effect
        float scale = 0.85f + 0.15f * Math.max(0f, Math.min(1f, progress * 5f));
        int scaledW = (int)(boxWidth * scale);
        int scaledH = (int)(boxHeight * scale);
        int sx = getWidth() / 2 - scaledW / 2;
        int sy = getHeight() / 2 - scaledH / 2;

        // ★ outer glow
        for (int i = 6; i > 0; i--) {
            g2d.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(),
                    (int)(15 * fadeAlpha)));
            g2d.fillRoundRect(sx - i * 3, sy - i * 3, scaledW + i * 6, scaledH + i * 6, 30, 30);
        }

        // box background with gradient
        GradientPaint bg = new GradientPaint(sx, sy, new Color(15, 15, 35, (int)(240 * fadeAlpha)),
                sx, sy + scaledH, new Color(35, 30, 60, (int)(240 * fadeAlpha)));
        g2d.setPaint(bg);
        g2d.fillRoundRect(sx, sy, scaledW, scaledH, 28, 28);

        // border
        g2d.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(),
                (int)(255 * fadeAlpha)));
        g2d.setStroke(new BasicStroke(3f));
        g2d.drawRoundRect(sx, sy, scaledW, scaledH, 28, 28);

        //  text (fade shesh hole full clarity)
        if (fadeAlpha > 0.4f) {
            float textAlpha = Math.min(1f, (fadeAlpha - 0.4f) / 0.6f);

            // Title
            String title = "LEVEL " + level;
            g2d.setFont(new Font("Arial", Font.BOLD, 44));
            g2d.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(),
                    (int)(255 * textAlpha)));
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = getWidth() / 2 - fm.stringWidth(title) / 2;
            g2d.drawString(title, titleX, sy + 75);

            // Subtitle
            String subtitle = switch (level) {
                case 2 -> " Thunder,  Ice &  Net Arrows Unlocked!";
                case 3 -> " MASTER HUNTER";
                default -> "Get Ready!";
            };
            g2d.setFont(new Font("Arial", Font.BOLD, 19));
            g2d.setColor(new Color(255, 255, 255, (int)(255 * textAlpha)));
            FontMetrics fm2 = g2d.getFontMetrics();
            int subX = getWidth() / 2 - fm2.stringWidth(subtitle) / 2;
            g2d.drawString(subtitle, subX, sy + 130);

            // divider line
            g2d.setColor(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(),
                    (int)(120 * textAlpha)));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(getWidth()/2 - 100, sy + 155, getWidth()/2 + 100, sy + 155);

            // hint text
            g2d.setFont(new Font("Arial", Font.ITALIC, 13));
            g2d.setColor(new Color(180, 180, 190, (int)(200 * textAlpha)));
            String hint = "Starting next level...";
            FontMetrics fm3 = g2d.getFontMetrics();
            int hintX = getWidth() / 2 - fm3.stringWidth(hint) / 2;
            g2d.drawString(hint, hintX, sy + scaledH - 25);
        }

    }
    private void drawLowLivesAlert(Graphics2D g2d) {
        String text = "ONLY 5 LIVES LEFT!";

        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        int boxX = getWidth()/2 - textWidth/2 - 25;
        int boxY = 30;
        int boxW = textWidth + 50;
        int boxH = 55;

        // pulsing effect (blinking alert)
        long time = System.currentTimeMillis();
        float pulse = (float) (0.6 + 0.4 * Math.sin(time / 150.0));

        // background box
        g2d.setColor(new Color(180, 0, 0, (int)(200 * pulse)));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // border
        g2d.setColor(new Color(255, 60, 60));
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // text
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, boxX + 25, boxY + 37);
    }

    private Image getBackgroundForLevel(int level) {
        return switch (level) {
            case 2 -> snowBg;
            case 3 -> level3Bg;
            default -> forestBg;
        };
    }


            private void drawClouds (Graphics2D g2d){
            int cloudOffset = engine.getCloudOffset();
            g2d.setColor(new Color(255, 255, 255, 180));
            drawCloud(g2d, (200 - cloudOffset / 3 + getWidth()) % getWidth(), 80);
            drawCloud(g2d, (500 - cloudOffset / 5 + getWidth()) % getWidth(), 120);
            drawCloud(g2d, (750 - cloudOffset / 4 + getWidth()) % getWidth(), 60);
        }

            private void drawCloud (Graphics2D g2d,int x, int y){
            g2d.fillOval(x, y, 80, 35);
            g2d.fillOval(x + 25, y - 20, 60, 40);
            g2d.fillOval(x + 50, y, 70, 35);
        }
            public void drawScorePopups (Graphics2D g2d){
            for (GameEngine.ScorePopup sp : engine.getScorePopups()) {
                float alpha = Math.min(1f, sp.life / 3f);
                boolean hasMultiplier = sp.text.contains("X");

                if (hasMultiplier) {
                    g2d.setFont(new Font("Arial", Font.BOLD, 22));
                    g2d.setColor(new Color(255, 150, 0, (int) (alpha * 255)));

                } else {
                    g2d.setFont(new Font("Arial", Font.BOLD, 18));
                    g2d.setColor(new Color(255, 255, 100, (int) (alpha * 255)));
                }
                g2d.drawString(sp.text, (int) sp.x, (int) sp.y);

            }
        }


    private void drawGameOver(Graphics2D g2d){

        // dark overlay
        g2d.setColor(new Color(0,0,0,170));
        g2d.fillRect(0,0,getWidth(),getHeight());


        // Game Over Box

        int boxWidth = 420;
        int boxHeight = 300;

        int x = getWidth()/2 - boxWidth/2;
        int y = getHeight()/2 - boxHeight/2;


        g2d.setColor(new Color(20,20,40,230));
        g2d.fillRoundRect(x, y, boxWidth, boxHeight, 25, 25);


        g2d.setColor(new Color(255,70,70));
        g2d.setStroke(new BasicStroke(3));

        g2d.drawRoundRect(x, y, boxWidth, boxHeight, 25, 25);

        // Title

        g2d.setFont(new Font("Arial",Font.BOLD,40));
        g2d.setColor(Color.RED);

        g2d.drawString("GAME OVER", x+70, y+60);

        // Stats text

        g2d.setFont(new Font("Arial",Font.BOLD,20));
        g2d.setColor(Color.WHITE);

        int startY = y+110;

        g2d.drawString("Current Score : " + engine.getStats().getScore(), x+50, startY);

        g2d.drawString("Highest Score : " + engine.getStats().getHighScore(), x+50, startY+35);

        g2d.drawString("Coins Earned : +" + engine.getStats().getCoinsEarned(), x+50, startY+70);

        g2d.drawString("Birds Killed : " + engine.getStats().getBirdsKilled(), x+50, startY+105);



        if(engine.isBombExploded()){

            g2d.setColor(new Color(255,100,0,100));
            g2d.fillOval(0,0,getWidth(),getHeight());

        }

    }
    private void positionRestartButton() {
        int boxWidth = 420;
        int boxHeight = 300;
        int x = getWidth()/2 - boxWidth/2;
        int y = getHeight()/2 - boxHeight/2;

        int btnWidth = 180;
        int btnHeight = 45;
        int btnX = getWidth()/2 - btnWidth/2;
        int btnY = y + boxHeight -55 ;

        restartBtn.setBounds(btnX, btnY, btnWidth, btnHeight);
        restartBtn.revalidate();
    }

    public void setRestartListener(Runnable listener) {
        restartBtn.addActionListener(e -> {
            engine.getStats();
            listener.run();
        });
    }
}




