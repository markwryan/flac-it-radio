package com.markryan.flac_it.player;

/**
 * Created by markryan on 1/8/14.
 */
import org.kc7bfi.jflac.apps.Decoder;

import java.io.BufferedReader;
import java.io.File;

import javax.media.*;



import javax.sound.sampled.*;

public class FlacTrackPlayer{
    private File track;
    private Player player;


    //Constructor takes the name to save decoded track as in the form of a string ex:"track1.wav"
    public FlacTrackPlayer(String trackName) {
        player = null;
        track = new File(trackName);
        Mixer mixer = AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]);
        try {
            mixer.open();
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            System.out.println("huh?");
            e.printStackTrace();
        }



    }
    //Load decodes the track and creates a Realized Player
    public void load(String filename){
        Decoder p1 = new Decoder();
        try{
            p1.decode(filename,track.getPath());
            player = Manager.createRealizedPlayer(track.toURL());


        }catch(Exception e){e.printStackTrace();}
    }
    //Play starts the track from the beginning or from the last played part
    public void play(){
        try{
            player.start();
        }catch(Exception e){e.printStackTrace();}
    }
    //Stops the music from playing
    public void pause(){
        try{
            player.stop();
        }catch(Exception e){e.printStackTrace();}
    }

    //Stops the track, resets it to the beginning
    public void rewind(){
        player.stop();
        player.setMediaTime(Player.RESET);
    }

    //returns the length of the track in the form of seconds
    public double getTime(){
        return player.getDuration().getSeconds();
    }



}
