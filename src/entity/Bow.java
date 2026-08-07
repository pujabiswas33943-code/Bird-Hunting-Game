package entity;
import java.awt.*;
public class Bow {
    private int x,y;
    private int dragStartX, dragStartY;
    private int dragCurrentX, dragCurrentY;
    private boolean isDragging;
    private boolean drawing;
    private double angle;
    private int drawPower;
    private int width = 20;
    private int height = 80;
    private static final int MAX_DRAW = 30;

    public Bow(int x,int y){
        this.x=x;
        this.y=y;
    }
    public void draw(Graphics2D g2d) {

        Graphics2D g = (Graphics2D) g2d.create();

        // Smooth edges
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // SHADOW
        g.setColor(new Color(0,0,0,80));
        g.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawArc(x-6, y-32, 82, 102, 90, 180);

        // OUTER GLOW
        g.setColor(new Color(255,215,100,120));
        g.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawArc(x-10, y-35, 80, 100, 90, 180);

        // MAIN WOOD
        GradientPaint wood = new GradientPaint(x, y-40, new Color(90,50,20),
                x+70, y+40, new Color(180,120,60)
        );

        g.setPaint(wood);

        g.setStroke(new BasicStroke(6f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        g.drawArc(x-10, y-35, 80, 100, 90, 180);

        // INNER HIGHLIGHT
        g.setColor(new Color(255,220,170,120));

        g.setStroke(new BasicStroke(2f));
        g.drawArc(x-5, y-30, 70, 90, 90, 180);

        //  STRING
        g.setColor(new Color(240,240,240));

        g.setStroke(new BasicStroke(2f));

        g.drawLine(x-10, y-35, x-10, y+35);


        // String middle point drag direction e sorbe
        int midX = x - 10;
        int midY = y;

        if (drawing && drawPower > 0) {
            // Drag direction dike string stretch
            midX += (int)(Math.cos(angle) * (drawPower / 1.5));
            midY += (int)(Math.sin(angle) * (drawPower / 1.5));
        }

        g.setColor(new Color(240, 240, 240));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(x - 10, y - 35, midX, midY);
        g.drawLine(midX,   midY,   x - 10, y + 35);
        if (isDragging) drawDragGuide(g2d);

        g.dispose();
    }
    public void startDrag(int mouseX, int mouseY) {
        dragStartX = mouseX;
        dragStartY = mouseY;
        dragCurrentX = mouseX;
        dragCurrentY = mouseY;
        isDragging = true;
        drawing = true;
    }
    public void updateDrag(int mouseX, int mouseY) {
        if (!isDragging) return;
        dragCurrentX = mouseX;
        dragCurrentY = mouseY;
        double dx =  mouseX - getCenterX() ;
        double dy = mouseY - getCenterY() ;
        angle = Math.atan2(dy, dx);
        double dist = Math.sqrt(dx * dx + dy * dy);
        drawPower = (int) Math.min(dist / 3, MAX_DRAW);
    }

    public double releaseDrag() {
        isDragging = false;
        drawing = false;
        drawPower = 0;
        return angle;
    }
    public boolean isDragging() { return isDragging; }
    public void aimAt(int mouseX, int mouseY) {
        if (isDragging) return;
        double dx = mouseX - getCenterX();
        double dy = mouseY - getCenterY();
        angle = Math.atan2(dy, dx);
    }

    private void drawDragGuide(Graphics2D g2d) {
        // for showing direction
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 0, new float[]{8, 5}, 0));
        double tx = getCenterX() + Math.cos(angle+Math.PI) * 120;
        double ty = getCenterY() + Math.sin(angle+Math.PI) * 120;
        g2d.drawLine((int) getCenterX(), (int) getCenterY(), (int) tx, (int) ty);
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public double getAngle() { return angle; }
    public double getCenterX() { return x + width / 2.0; }
    public double getCenterY() { return y + height / 2.0; }
    public void updatePosition(int x, int y) {
        this.x=x;
        this.y=y;
    }
}
