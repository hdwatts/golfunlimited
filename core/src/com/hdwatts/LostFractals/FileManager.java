package com.hdwatts.LostFractals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.sql.SQLException;

/**
 * Created by Dean Watts on 1/25/2015.
 */
public class FileManager {
    FileHandle handle;
    FileHandle scores;
    boolean exists;
    public int currLevel;



    public FileManager(){
        handle = Gdx.files.local("progress.sav");
        scores = Gdx.files.local("scores.sav");
        exists = handle.exists();
        try {
            if (handle.exists()) {
                String str = handle.readString();
                int curr = str.lastIndexOf("Level ");
                currLevel = Integer.parseInt(str.substring(curr+6, str.indexOf(",",curr)));
            }else{
                throw(new RuntimeException());
            }
        }catch(RuntimeException e){
            System.out.println("TAMPERING DETECTED! Resetting Save!");
            handle.writeString("Level 1, Seed 44375873822l, Height 11\n",false);
            String str = handle.readString();
            int curr = str.lastIndexOf("Level ");
            currLevel = Integer.parseInt(str.substring(curr+6, str.indexOf(",",curr)));
            //handle.writeString("Level 1, Seed 44375873822l,",true);
        }
        if(!scores.exists()){
            scores.writeString(",-1,",false);
        }
    }

    public int getScore(int level){
        String str = scores.readString();
        int start = -1;
        for(int a = 0; a < level; a++){
            start = str.indexOf(",", start+1);
        }
        int finish = str.indexOf(",", start+1);
        try {
            return Integer.parseInt(str.substring(start + 1, finish));
        }catch(RuntimeException e){
            return -1;
        }
    }
    public void addScore(int level, int score){
        String str = scores.readString();
        int start = -1;
        for(int a = 0; a < level; a++){
            start = str.indexOf(",", start+1);
        }
        int finish = str.indexOf(",", start+1);
        if(finish == -1){
            scores.writeString(score+",",true);
         //   try{
                //GDXGame.dbManager.addScore(level,score);
         //   }catch(SQLException e){
         //       System.out.println(e.toString());
           // }
        }else {
            if (score < Integer.parseInt(str.substring(start + 1, finish)) || str.substring(start + 1, finish).equals("-1")){
                str = str.substring(0, start) + "," + score + "" + str.substring(finish);
                //try{
                    //GDXGame.dbManager.addScore(level,score);
                //}catch(SQLException e){
                 //   System.out.println(e.toString());
                //}
            }else{
                System.out.println("YOU GOT HIGHER");
            }
            scores.writeString(str, false);
        }

    }

    public long getSeed(int level){
        String str = handle.readString();
        int curr = str.indexOf("Level "+level);
        curr = str.indexOf(",", curr);
        //long seed = Long.parseLong("44375873822");
        long seed = Long.parseLong(str.substring(curr + (" Seed  ").length(), str.indexOf(",", curr+1)-1));
        return seed;
    }
    public int getHeight(int level){
        String str = handle.readString();
        int curr = str.indexOf("Level "+level);
        curr = str.indexOf(",", curr);
        curr = str.indexOf(",", curr+1);
        return Integer.parseInt(str.substring(curr + (" Height  ").length(),str.indexOf(("\n"),curr)));

    }

    public void addSeed(long seed, int height){
        String str = handle.readString();
        int level = Integer.parseInt(str.substring(str.lastIndexOf("Level ") + 6, str.indexOf(",", str.lastIndexOf("Level ")+6)));
        level++;
        if(!str.contains(" Seed "+seed+"l,")){
            handle.writeString("Level "+level+", Seed "+seed+"l, Height "+height+"\n",true);
        }
    }

    public void delete(){
        scores.delete();
        handle.delete();
        scores.writeString(",-1,",false);
        handle.writeString("Level 1, Seed 44375873822l, Height 11\n",false);
        String str = handle.readString();
        int curr = str.lastIndexOf("Level ");
        currLevel = Integer.parseInt(str.substring(curr+6, str.indexOf(",",curr)));

    }
}
