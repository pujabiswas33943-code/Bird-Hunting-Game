 package engine;

import entity.Bird;
import java.util.List;
import java.util.Random;

public class Spawner {
    private int spawnTimer;
    private int spawnInterval;
    private int wave;
    private Random rand;

    public Spawner() {
        spawnTimer    = 0;
        spawnInterval = 120;
        wave          = 1;
        rand          = new Random();
    }

    public  void update(List<Bird> birds, int score, int panelHeight,int level) {
        switch (level) {
            case 1 -> spawnInterval = 140; // slow spawn
            case 2 -> spawnInterval = 90;  // medium spawn
            default -> spawnInterval = 55; // fast spawn (level 3)
        }

        //wave          = 1 + score / 200;      //for each 200 score 1 level increase

        spawnTimer++;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            spawnBirds(birds,panelHeight,level);
        }
    }

    private void spawnBirds(List<Bird> birds, int panelHeight,int level) {
        int count = 1 + rand.nextInt(Math.min(wave, 3));  //maximum 3 birds at a time
        for (int i = 0; i < count; i++) {
            double startY = 100 + rand.nextDouble() *(panelHeight-200);  // spawn bird between 100 t0 500 px
            double speed = switch (level) {
                case 1  -> 2.0 + rand.nextDouble() * 1.0;  // slow
                case 2  -> 2.5 + rand.nextDouble() * 1.0;  // medium
                default -> 3.5 + rand.nextDouble() * 2.0;  // fast
            };

            birds.add(new Bird(-60, startY, pickType(),speed));
        }
    }

    private Bird.BirdType pickType() {
        int r = rand.nextInt(100);
        if (r < 20) return Bird.BirdType.bomb;
        if (r < 45) return Bird.BirdType.bird2;
        if (r<70) return Bird.BirdType.bird3;
        return Bird.BirdType.bird1;
    }
}