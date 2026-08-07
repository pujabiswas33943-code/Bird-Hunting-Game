package entity;

import java.util.HashMap;
import java.util.Map;

public class PlayerStats {
    private int score;
    private int lives;
    private int coins;
    private int combo;
    private int maxCombo;
    private int highScore;
    private int birdsKilled;
    private int coinsEarned;
    private Map<Arrow.ArrowType, Integer> arrowCounts;

    public PlayerStats(){
        coins=150;
        resetExceptCoins();
    }
    public  void reset(){
        resetExceptCoins();
    }
    private void resetExceptCoins(){
        score = 0;
        lives = 20;
        combo = 0;
        maxCombo = 0;
        birdsKilled = 0;
        coinsEarned = 0;
        arrowCounts = new HashMap<>();
        arrowCounts.put(Arrow.ArrowType.NORMAL, -1);
        arrowCounts.put(Arrow.ArrowType.POWER, 5);
        arrowCounts.put(Arrow.ArrowType.THUNDER, 10);
        arrowCounts.put(Arrow.ArrowType.ICE, 10);
        arrowCounts.put(Arrow.ArrowType.NET, 10);

    }

    public boolean useArrow(Arrow.ArrowType type){
        int count = arrowCounts.getOrDefault(type, 0);
        if(count == -1)   //for normal arrow ,,since normall arrow is unlimited
            return true;
        if(count <= 0)
            return false;
        arrowCounts.put(type, count-1);
        return true;
    }
    public void addArrows(Arrow.ArrowType type,int amount){
        if(type == Arrow.ArrowType.NORMAL)
            return;

        if (arrowCounts.containsKey(type)) {

            int currentAmount = arrowCounts.get(type);
            arrowCounts.put(type, currentAmount + amount); // add arrow with previous arrow
        } else {

            arrowCounts.put(type, amount);
        }
    }
    public int getArrowCount(Arrow.ArrowType type){
        return arrowCounts.getOrDefault(type,0);
    }
    public void incrementCombo(){
        combo++;
        if(combo > maxCombo)
            maxCombo = combo;
    }
    public void addRawScore(int points){
        score += points;
        if(score > highScore){
            highScore = score;
        }
    }

    public void resetCombo(){
        combo = 0;
    }
    public void loseLife(){
        lives = Math.max(0,lives - 1);
    }
    public void addCoins(int amount){
        coins += amount;
    }
    public boolean spendCoins(int amount){
        if(coins >= amount){
            coins -= amount;
            return true;
        }
        return false;
    }
public  void incrementBirdKilled(){
        birdsKilled++;
}

public  int getBirdsKilled(){
    return birdsKilled;
}
    public void addCoinsEarned(int amount){
        coinsEarned += amount;
    }


    public int getCoinsEarned(){
        return coinsEarned;
    }
    public int getScore(){
        return score;
    }
    public int getLives(){
        return lives;
    }
    public int getCoins(){
        return coins;
    }
    public void setCoins(int coins){
        this.coins=coins;
    }
    public int getHighScore(){
        return highScore;
    }
    public void setLives(int lives){
        this.lives = lives;
    }


    public void setHighScore(int highScore){
        this.highScore = highScore;
    }

    public boolean isGameOver(){
        return lives <= 0;
    }

    public int getCombo() {
        return combo;
    }


}
