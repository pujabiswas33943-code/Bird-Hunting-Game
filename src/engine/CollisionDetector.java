package engine;

import entity.Arrow;
import entity.Bird;

import java.util.List;

public class CollisionDetector {
    public interface HitCallback{
        void onHit(Arrow arrow, Bird bird);
    }
    public interface EscapeCallback{
        void onEscape(Bird bird);
    }
    public void check(List<Arrow> arrows, List<Bird> birds, int panelWidth, HitCallback Hit, EscapeCallback Escape ){
        for(Arrow arrow : arrows){
            if(!arrow.isActive())
                continue;
            for(Bird bird: birds){
                if(!bird.isActive())
                    continue;
                if(arrow.hasHit(bird))
                    continue;
                if(isColliding(arrow, bird)){
                    Hit.onHit(arrow,bird);
                    arrow.markHit(bird);

                    if(!arrow.isPiercing()) {
                        arrow.setActive(false);
                    }
                }
            }
        }
        for(Bird bird:birds){
            if(bird.isActive() && bird.getX() > panelWidth){
                bird.setActive(false);
                Escape.onEscape(bird);

            }
        }
    }

    private boolean isColliding(Arrow arrow, Bird bird){
        return arrow.getX() > bird.getX()
                && arrow.getX() < bird.getX() + bird.getWidth()
                && arrow.getY() > bird.getY()
                && arrow.getY() < bird.getY() + bird.getHeight();
    }
}
