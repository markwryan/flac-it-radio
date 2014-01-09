package com.markryan.flac_it.player.util;

/**
 * Created by markryan on 1/8/14.
 */
import com.markryan.flac_it.player.FlacTrackPlayer;
import com.markryan.flac_it.view.SingleTrackView;

import java.awt.Color;


public class SingleTrackTimer implements Runnable{

    /**
     * @param args
     */
    private int minutes, seconds;
    private SingleTrackView flacGUI;
    private boolean countDown;

    public SingleTrackTimer(SingleTrackView gui, int min, int sec){
        flacGUI = gui;
        minutes = min;
        seconds = sec+1;

        if(seconds < 10){
            flacGUI.setTimeText(minutes+":0"+seconds);
        }else if(seconds >= 10 && minutes >= 0){
            flacGUI.setTimeText(minutes+":"+seconds);
        }

    }

    public void run() {
        while(minutes>=0 && seconds>=0 && countDown == true){
            if(seconds>0){
                seconds--;
            }else if(seconds == 0 && minutes > 0){
                seconds = 59;
                minutes--;
            }
            if(seconds <= 15 && minutes == 0){
                flacGUI.setBackground(Color.RED);
            }
            if(seconds == 0 && minutes == 0){
                flacGUI.endTrack();
            }
            try {
                if(seconds < 10){
                    flacGUI.setTimeText(minutes+":0"+seconds);
                }else if(seconds >= 10 && minutes >= 0){
                    flacGUI.setTimeText(minutes+":"+seconds);
                }else if(seconds == 0 && minutes == 0){
                    flacGUI.setTimeText("Track Finished");// Replaced to reset player.. "Auto Eject?"
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
    public void setCountDown(boolean count){
        countDown = count;
    }


}

