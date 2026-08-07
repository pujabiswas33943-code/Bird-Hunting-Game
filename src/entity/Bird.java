package entity;

import javax.swing.ImageIcon;
import java.awt.*;

public class Bird {

    private double x,y;
    private double speedX;
    private boolean active;
    private int health,maxHealth;
    private BirdType type;
    private int scoreValue,coinValue;
    private int width,height;

    private double wingAngle = 0;
    private int wingDir=1;
    private Image bombImage;
public enum BirdType{ bird1,bird2,bird3,bomb};

    private boolean frozen;
    private int frozenTimer;
    private boolean electric;
    private int electricTimer;
    private boolean caught;
    private int caughtTimer;
    private boolean iced;
    private int icedTimer;
private boolean dead = false;

    public Bird(double x,double y,BirdType type,double speedX){
        this.x=x;
        this.y=y;
        this.speedX=speedX;
        this.active=true;
this.type=type;
if(type == BirdType.bomb){
    try {
        bombImage = new ImageIcon(getClass().getResource("/images/bomb.png")).getImage();

    }catch (Exception e){
        bombImage = null;
    }
}
setupBird();
    }
    public void setupBird() {
        switch (type) {
            case bird1 -> {health=1; maxHealth = 1; width = 50; height =32; scoreValue = 10; coinValue =5;}
            case bird2 -> {health=1; maxHealth = 1; width = 50; height =38; scoreValue = 20; coinValue =10;}
            case bird3 -> {health=1; maxHealth = 1; width = 52; height =34; scoreValue = 30; coinValue =15;}
            case bomb -> {health = 1; maxHealth = 1; width = 70; height = 70; scoreValue = 0; coinValue = 0; speedX = 2.0 + Math.random() * 1.5;}
        }
    }



    public void update(int panelWidth,int panelHeight){

       if(electric){
           electricTimer--;
           if(electricTimer <= 0)
               electric = false;
       }
        if (iced) {

           icedTimer--;
           if(icedTimer <= 0)
               iced =false;
        }

        if (caught){
            caughtTimer--;
            if (caughtTimer <= 0)
                caught = false;
            frozen = false;
        }
        if(dead){
            // bird stays for effect

            if(!electric && !iced && !caught){
                active = false;
            }
            return;
        }
        x+=speedX;
        if(x>panelWidth+100) {
            active = false;
        }

           wingAngle+= wingDir * 8;
        if(wingAngle > 40) {
            wingDir = -1;
        } else if (wingAngle < -40) {
            wingDir = 1;

        }
    }
    public void draw(Graphics2D g2d){


           Color bodyColor = switch (type){
               case bird1 -> new Color(40,40,40);
               case bird2 -> new Color(255,210,0);
               case bird3 -> new Color(200,50,50);
               case bomb -> new Color(30,30,30);

            };
           int bx=(int)x, by = (int) y;

           if(!active){
               if(electric)
                   drawElectricEffect(g2d,bx,by);
               if(iced)
                   drawIceEffect(g2d,bx,by);
               if (caught)
                   drawNetEffect(g2d,bx,by);
               return;
           }


        if(type == BirdType.bomb){
            if(bombImage != null){
                g2d.drawImage(bombImage,bx,by,width,height,null);
            }
            return;
        }
           //BODY
        g2d.setColor(bodyColor);
        g2d.fillOval(bx,by+height/4, width, height / 2);

        //HEAD
        g2d.fillOval(bx+width-width / 3, by+height/6, width / 3, height /2);

        //BEAK
        g2d.setColor(new Color(255,160,0));
        int[] bkx = {bx + width, bx + width + 8, bx + width};
        int[] bky = {by + height / 3, by + height /2, by + height * 2 / 3 };
        g2d.fillPolygon(bkx,bky,3);

       // EYE
        g2d.setColor(Color.WHITE);
        g2d.fillOval(bx + width - width/4, by + height / 4, 7, 7);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(bx + width - width /4 + 2, by + height / 4 + 2,3, 3);

        //WING
         Graphics2D wg=(Graphics2D) g2d.create();
        wg.rotate(Math.toRadians(wingAngle),bx+width/2, by + height / 2);
        wg.setColor(bodyColor.darker());
        wg.fillOval(bx + width / 4, by, width / 2, height /2);
        wg.dispose();

        if (electric) drawElectricEffect(g2d, bx, by);
        if (iced)     drawIceEffect(g2d, bx, by);
        if (caught)   drawNetEffect(g2d, bx, by);

        if(health < maxHealth && health > 0){
            g2d.setColor(Color.RED);
            g2d.fillRect(bx , by - 8, width, 5);

            g2d.setColor(Color.GREEN);
            g2d.fillRect(bx, by - 8, (int) (width * ((double) health / maxHealth)), 5);

        }

    }

    private void drawElectricEffect(Graphics2D g2d, int bx, int by) {

        Graphics2D g = (Graphics2D) g2d.create();
        java.util.Random r = new java.util.Random();

        g.setColor(new Color(255, 255, 0, 200));
        g.setStroke(new BasicStroke(2f));

        int centerX = bx + width / 2;
        int centerY = by + height / 2;

        // lightning rays
        for (int i = 0; i < 8; i++) {

            int x2 = centerX + r.nextInt(40) - 20;
            int y2 = centerY + r.nextInt(40) - 20;

            int midX = (centerX + x2) / 2 + r.nextInt(10) - 5;
            int midY = (centerY + y2) / 2 + r.nextInt(10) - 5;

            g.drawLine(centerX, centerY, midX, midY);
            g.drawLine(midX, midY, x2, y2);
        }

        // glow
        g.setColor(new Color(255, 255, 0, 80));
        g.fillOval(bx - 10, by - 10, width + 20, height + 20);

        g.dispose();
    }

    private void drawIceEffect(Graphics2D g2d, int bx, int by) {

        Graphics2D g = (Graphics2D) g2d.create();

        // ice glow
        g.setColor(new Color(150, 220, 255, 90));
        g.fillOval(bx - 15, by - 15, width + 30, height + 30);

        // ice border
        g.setColor(new Color(180, 240, 255, 200));
        g.setStroke(new BasicStroke(3f));
        g.drawOval(bx - 10, by - 10, width + 20, height + 20);

        // crack lines
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255, 255, 255, 120));

        for (int i = 0; i < 6; i++) {
            int x1 = bx + (int)(Math.random() * width);
            int y1 = by + (int)(Math.random() * height);

            int x2 = x1 + (int)(Math.random() * 20 - 10);
            int y2 = y1 + (int)(Math.random() * 20 - 10);

            g.drawLine(x1, y1, x2, y2);
        }

        g.dispose();
    }

    private void drawNetEffect(Graphics2D g2d, int bx, int by) {

        Graphics2D g = (Graphics2D) g2d.create();

        g.setColor(new Color(220, 220, 220, 160));
        g.setStroke(new BasicStroke(1f));

        int step = 10;

        // vertical lines
        for (int x = bx - 5; x <= bx + width + 5; x += step) {
            g.drawLine(x, by - 5, x, by + height + 5);
        }

        // horizontal lines
        for (int y = by - 5; y <= by + height + 5; y += step) {
            g.drawLine(bx - 5, y, bx + width + 5, y);
        }

        // outer wrap
        g.setColor(new Color(255, 255, 255, 100));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(bx - 6, by - 6, width + 12, height + 12, 10, 10);

        g.dispose();
    }


    public  void hit(int damage){
        health-=damage;
        if(health <= 0)
            dead = true;
    }

    public void setElectric(boolean e){
        electric = e;
        electricTimer = 60;

    }
    public  void freezeWithIce(int duration)
    {
        frozen = true;
        iced = true;
        frozenTimer = duration;
        icedTimer = duration;
    }

    public void freezeWithNet(int duration){
        frozen = true;
        frozenTimer = duration;

    }

    public  void setCaught(boolean e){
        caught = e;
        caughtTimer = 90;
    }
    public void setActive(boolean a){
        active=a;
    }
    public boolean isActive()
    {
        return active;
    }

    public boolean isElectric(){
        return electric;
    }
    public boolean isIced(){
        return iced;
    }

    public boolean isCaught(){
        return caught;
    }
    public boolean isDead(){
        return dead;
    }
    public double getX(){return x;}
    public double getY(){
        return y;
    }
    public int getWidth(){ return width;}
    public int getHeight(){ return height;}
    public int getScoreValue(){ return scoreValue;}
    public int getCoinValue(){
        return coinValue;
    }

    public BirdType getType() {
        return type;
    }
}
