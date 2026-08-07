package engine;

import entity.PlayerStats;

import java.io.*;

public class SaveManager {


    public static void save(String playerName, PlayerStats stats){

        try{

            File folder = new File("players");

            if(!folder.exists()){
                folder.mkdir();
            }


            PrintWriter writer = new PrintWriter(new FileWriter("players/" + playerName + ".txt"));


            writer.println("Coins=" + stats.getCoins());
            writer.println("HighScore=" + stats.getHighScore());

            writer.close();


        }catch(Exception e){
            e.printStackTrace();
        }

    }



    public static void load(String playerName, PlayerStats stats){

        try{

            File file = new File("players/" + playerName + ".txt");


            if(!file.exists()){
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while((line = reader.readLine()) != null){

                String data[] = line.split("=");

                if(data[0].equals("Coins")){
                    stats.setCoins(Integer.parseInt(data[1]));
                }
                else if(data[0].equals("HighScore")){
                    stats.setHighScore(Integer.parseInt(data[1]));
                }

            }
            reader.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static boolean exists(String name){

        File file = new File("players/" + name + ".txt");

        return file.exists();

    }
    public static PlayerStats loadStats(String name){

        PlayerStats stats = new PlayerStats();

        load(name, stats);

        return stats;

    }

}