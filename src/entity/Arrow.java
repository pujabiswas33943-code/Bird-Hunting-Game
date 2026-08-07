package entity;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
public class Arrow
{



    public enum ArrowType { NORMAL, POWER, THUNDER, ICE, NET };
    private double  x,y;
    private double speedX,speedY;
    private boolean active;
   private double angle;
    private ArrowType type;
    private int damage;
    private Color arrowColor;
    private Set<Bird> hitBirds = new HashSet<>();
    public Arrow(double x, double y,double targetX,double targetY, double angle,ArrowType type){
    this.x=x;
    this.y=y;
    this.angle=angle;
    this.active=true;
    this.type=type;
setupArrow();

}

    protected void setupArrow() {
        switch (type) {
            case NORMAL  -> { damage = 1; arrowColor = new Color(180, 120, 40);  speedX = Math.cos(angle) * 14; speedY = Math.sin(angle) * 14; }
            case POWER   -> { damage = 3; arrowColor = new Color(180, 120, 40);   speedX = Math.cos(angle) * 10; speedY = Math.sin(angle) * 10; }
            case THUNDER -> { damage = 2; arrowColor = new Color(255, 230, 0);   speedX = Math.cos(angle) * 18; speedY = Math.sin(angle) * 18; }
            case ICE     -> { damage = 1; arrowColor = new Color(100, 200, 255); speedX = Math.cos(angle) * 12; speedY = Math.sin(angle) * 12; }
            case NET     -> { damage = 1; arrowColor = new Color(200, 200, 200); speedX = Math.cos(angle) * 9;  speedY = Math.sin(angle) * 9;  }
        }
    }


public void update(){
    if(!active)
        return;
    x+=speedX;
    y+=speedY;

    if(x>2000 || x<-100 || y<-100 || y>2000)
    {
        active=false;
    }
}
public void draw(Graphics2D g2d) {
    if (!active)
        return;


    Graphics2D ag = (Graphics2D) g2d.create();
    ag.translate(x, y);
    ag.rotate(Math.atan2(speedY, speedX));

    if (type == ArrowType.NET) {
        ag.setColor(arrowColor);
        ag.setStroke(new BasicStroke(1.5f));
        for (int i = -15; i <= 15; i += 6) {
            ag.drawLine(-20, i, 20, i);
            ag.drawLine(i, -10, i, 10);
        }
    }
    else{
        //main body
        ag.setColor(arrowColor);
    ag.fillRect(-30, -2, 60, 4);
    //tip
        ag.setColor(Color.LIGHT_GRAY);
        int[] tx = {30, 42, 30};
        int[] ty = {-4, 0, 4};
        ag.fillPolygon(tx, ty, 3);
 //last part of arrow
        ag.setColor(Color.WHITE);
        ag.fillRect(-30, -5, 8, 10);

        if (type == ArrowType.THUNDER) {
            ag.setColor(new Color(255, 255, 100, 180));
            ag.setStroke(new BasicStroke(2f));
            ag.drawLine(-40, 0, 40, 0);
        } else if (type == ArrowType.ICE) {
            ag.setColor(new Color(130, 220, 255, 200));
            ag.fillOval(-10, -10, 20, 20);
        }
    }
    ag.dispose();
}

public  ArrowType getType(){
    return type;
}
    public boolean isActive () {
        return active;
    }
    public double getX () {
        return x;
    }
    public double getY () {
        return y;
    }
    public void setActive ( boolean a){
        active = a;
    }
    public boolean hasHit(Bird bird) {
        return hitBirds.contains(bird);
    }

    public void markHit(Bird bird) {
        hitBirds.add(bird);
    }

    public boolean isPiercing() {
        return type == ArrowType.THUNDER;
    }

}



