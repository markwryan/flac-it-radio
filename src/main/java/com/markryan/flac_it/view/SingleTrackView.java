package com.markryan.flac_it.view;

/**
 * Created by markryan on 1/8/14.
 *
 * SingleTrackView displays a single track player with song info, player controls, track timer
 * By default, the player loads three of these players on the left side of the main display view
 */
import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

import com.markryan.flac_it.controller.FlacItRadioMain;
import com.markryan.flac_it.player.FlacTrackPlayer;
import com.markryan.flac_it.player.util.SingleTrackTimer;

public class SingleTrackView extends JPanel implements Runnable, DropTargetListener{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public FlacTrackPlayer flacPlayer;
    private FlacItRadioMain db;
    private JButton playPause;
    private JButton eject;
    private JButton back;
    private JButton cue;
    private ImageIcon playIcon = new ImageIcon("play.gif");
    private ImageIcon loadIcon = new ImageIcon("eject.gif");
    private ImageIcon pauseIcon = new ImageIcon("pause.gif");
    private ImageIcon stopIcon = new ImageIcon("stop.gif");
    private ImageIcon rewindIcon = new ImageIcon("rewind.gif");

    private JLabel artistText, titleText, timeText, backgroundText, labelText, albumText;
    //private JPanel panel;
    private boolean paused;
    private boolean hasTrack;
    private SingleTrackTimer timer;
    private String artist,album,title;
    //private JTextArea lyrics;
    private int playPauseCount;

    private Font labelFont,textFont;
    private int playerID;

    public SingleTrackView(FlacItRadioMain data, int id){
        playerID = id;
        db = data;
        flacPlayer = new FlacTrackPlayer(id+".wav");
        labelFont = new Font("Serif", Font.BOLD, 18);
        textFont = new Font("Serif", Font.BOLD, 25);
        playPauseCount = 0;
        this.setLayout(null);

        setVisible(true);

        artistText = new JLabel("LOAD A TRACK");
        titleText = new JLabel("click and drag a song here");
        albumText = new JLabel("");
        labelText = new JLabel("");
        timeText = new JLabel("0:00");

        playPause = new JButton(playIcon);
        paused=true;
        hasTrack=false;
        back = new JButton(rewindIcon);
        eject = new JButton(loadIcon);
        cue = new JButton("CUE");
        playPause.setEnabled(false);
        back.setEnabled(false);
        cue.setEnabled(false);

        //placement and size for first set
        playPause.setSize(89,49);
        playPause.setLocation(10,170);
        back.setSize(89,49);
        back.setLocation(113,170);
        eject.setSize(89,49);
        eject.setLocation(215,170);
        cue.setSize(60, 40);
        cue.setLocation(245,112);


        backgroundText = new JLabel();
        backgroundText.setSize(305, 140);
        backgroundText.setLocation(5,25);
        backgroundText.setOpaque(true);
        backgroundText.setBackground(Color.black);


        artistText.setLocation(10, 30);
        artistText.setSize(250,20);
        artistText.setFont(labelFont);
        artistText.setForeground(Color.WHITE);

        titleText.setLocation(10, 50);
        titleText.setSize(250, 20);
        titleText.setFont(labelFont);
        titleText.setForeground(Color.WHITE);

        albumText.setLocation(10, 70);
        albumText.setSize(250,20);
        albumText.setFont(labelFont);
        albumText.setForeground(Color.WHITE);

        labelText.setLocation(10, 90);
        labelText.setSize(250,20);
        labelText.setFont(labelFont);
        labelText.setForeground(Color.WHITE);


        timeText.setLocation(260,30);
        timeText.setSize(50, 30);
        timeText.setFont(textFont);
        timeText.setForeground(Color.WHITE);

        playPause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                play(false);
            }
        });
        cue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                play(true);
            }
        });
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                rewind();
            }
        });
        eject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ejectLoad();
            }
        });
        this.setFocusable(true);
        if(id == 1){
            playPause.setMnemonic(KeyEvent.VK_1);
            back.setMnemonic(KeyEvent.VK_2);
            eject.setMnemonic(KeyEvent.VK_3);
        }
        if(id == 2){
            playPause.setMnemonic(KeyEvent.VK_4);
            back.setMnemonic(KeyEvent.VK_5);
            eject.setMnemonic(KeyEvent.VK_6);
        }
        if(id == 3){
            playPause.setMnemonic(KeyEvent.VK_7);
            back.setMnemonic(KeyEvent.VK_8);
            eject.setMnemonic(KeyEvent.VK_9);
        }
        this.add(backgroundText);
        this.add(artistText);
        this.add(titleText);
        this.add(albumText);
        this.add(labelText);
        this.add(timeText);
        this.add(playPause);
        this.add(back);
        this.add(eject);
        this.add(cue);
        this.setLayout(null);
        this.setVisible(true);
        this.setEnabled(true);
        this.setBackground(Color.decode("#524D4A"));
        this.setComponentZOrder(artistText, 0);
        this.setComponentZOrder(timeText, 1);
        this.setComponentZOrder(titleText, 2);
        this.setComponentZOrder(labelText, 3);
        this.setComponentZOrder(albumText, 4);
        this.setComponentZOrder(cue, 5);
        this.setComponentZOrder(backgroundText, 6);


    }
    public void play(boolean isCue){
        if(paused){

            flacPlayer.play();
            Thread thread = new Thread(timer);
            timer.setCountDown(true);
            thread.start();
            paused = false;
            playPause.setIcon(pauseIcon);
            if(isCue == false){
                db.logSong(artist,album,title);
            }

        }else{
            flacPlayer.pause();
            timer.setCountDown(false);
            paused = true;
            playPause.setIcon(playIcon);
        }
    }

    public void rewind(){
        timer.setCountDown(false);
        double time = flacPlayer.getTime();
        int min = (int)time/60;
        int sec = (int)time % 60;
        timer = new SingleTrackTimer(this,min,sec);
        this.setBackground(Color.decode("#666666"));
        flacPlayer.rewind();
        paused = true;
        playPause.setIcon(playIcon);

    }
    public void ejectLoad(){
        if(hasTrack){
            if(!paused){
                flacPlayer.pause();
                playPause.setIcon(playIcon);
            }
            if(!paused){
                flacPlayer.pause();
                playPause.setIcon(playIcon);
            }
            eject.setIcon(loadIcon);
            timer.setCountDown(false);
            timer.setMinutes(0);
            timer.setSeconds(0);
            playPauseCount = 0;
            paused=true;
            flacPlayer.pause();
            playPause.setEnabled(false);
            back.setEnabled(false);
            cue.setEnabled(false);
            hasTrack = false;
            artistText.setText("LOAD A TRACK");
            titleText.setText("click and drag a song here");
            timeText.setText("0:00");
            albumText.setText("");
            labelText.setText("");
            this.setBackground(Color.decode("#666666"));
        }else{
            db.makeThread(playerID,this);
        }

    }

    public void run(){
        artist = db.getSelectedArtist();
        album = db.getSelectedAlbum();
        title = db.getSelectedTitle();
        String label = db.getLabelFromArtistAlbum(artist, album);
        artistText.setText(artist);
        titleText.setText(title);
        albumText.setText(album);
        labelText.setText(label);
        ResultSet resultSet = null;
        if(db.getSelectedArtist() != null && db.getSelectedTitle() != null && db.getSelectedAlbum() != null){
            resultSet = db.getDBInfo("SELECT path from MUSIC WHERE artist=\""+db.getSelectedArtist()+"\"and title=\""+db.getSelectedTitle()+"\"and album=\""+db.getSelectedAlbum()+"\"");

            try {
                if(!resultSet.next()){
                    resultSet = db.getDBInfo("SELECT path from PUSHBOX WHERE artist=\""+db.getSelectedArtist()+"\"and title=\""+db.getSelectedTitle()+"\"and album=\""+db.getSelectedAlbum()+"\"");
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            String path = null;
            try {
                resultSet.beforeFirst();
                while(resultSet.next()){
                    path = resultSet.getString("path");
                }
                eject.setEnabled(false);
                paused=true;
                flacPlayer.load(path);
                eject.setIcon(stopIcon);
                hasTrack = true;
                back.setEnabled(true);
                eject.setEnabled(true);
                playPause.setEnabled(true);
                cue.setEnabled(true);
                this.setBackground(Color.decode("#666666"));
                double time = flacPlayer.getTime();
                int min = (int)time/60;
                int sec = (int)time % 60;
                timer = new SingleTrackTimer(this,min,sec);
            }catch(Exception ex){JOptionPane.showMessageDialog(this, "Database Error 111."); ex.printStackTrace();}
        }else{
            albumText.setText("");
            labelText.setText("");
            artistText.setText("LOAD A TRACK");
            titleText.setText("Click and Drag a Song Here");

            timeText.setText("0:00");

        }




    }
    public void setTimeText(String text){
        timeText.setText(text);
    }
    public void dragEnter(DropTargetDragEvent arg0) {

    }

    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    public void drop(DropTargetDropEvent dtde) {
        try {
            if(hasTrack){
                ejectLoad();
                ejectLoad();
            }
            dtde.acceptDrop(1);
            ejectLoad();
        }catch(Exception e){e.printStackTrace();}
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method
    }
    public void endTrack(){

    }
}

