package com.markryan.flac_it.controller;

import com.markryan.flac_it.model.RadioShow;
import com.markryan.flac_it.util.NonEditableTableModel;
import com.markryan.flac_it.view.SingleTrackView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class FlacItRadioMain extends JFrame implements Runnable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JList artistDatabase;
    private JList albumDatabase;
    private JList titleDatabase;
    private JTable searchDatabase;
    private JTable pushDatabase;
    private JComboBox showNameBox;
    private JTextField searchBox;
    private JComboBox choices;
    private String selectedArtist;
    private String selectedTitle, selectedAlbum;
    private JButton searchButton;
    private JButton lyricsButton;
    private JButton pushBoxButton;
    private JTextField locationField;
    private JTextField userNameField;
    private JTextField passwordField;
    private JComboBox logging;
    private JLabel artistName;
    private JLabel albumName;
    private JLabel labelName;
    private JLabel titleName;
    private JLabel cdCoverArt;
    private JLabel showNameLabel;
    private JLabel back;
    private Font infoFont;
    private JLabel profaneLabel;
    private JComboBox profaneBox;
    private JButton profaneButton;


    private String mysqlPath, mysqlUser, mysqlPass, loggingOn;

    private JMenuBar menuBar;
    private JMenuItem preference;
    private JMenu file;

    private DefaultListModel artistModel;
    private DefaultListModel titleModel;
    private DefaultListModel albumModel;
    private DefaultTableModel searchModel,pushModel;
    private SingleTrackView gui1,gui2,gui3;

    private JPanel panel;
    private Thread t1,t2,t3;
    private RadioShow[] show;

    public FlacItRadioMain(){

        mysqlPath = "//192.168.10.3/music";
        mysqlUser = "root";
        mysqlPass = "";


        setSize(1200,750);
        this.setBackground(Color.decode("#6B7552"));
        selectedArtist = null;
        selectedTitle = null;
        selectedAlbum = null;
        artistModel = new DefaultListModel();
        albumModel = new DefaultListModel();
        titleModel = new DefaultListModel();
        searchModel = new NonEditableTableModel();
        pushModel = new NonEditableTableModel();
        searchBox = new JTextField(20);
        artistName = new JLabel("Artist: ");
        albumName = new JLabel("Album: ");
        labelName = new JLabel("Label: ");
        titleName = new JLabel("Title: ");
        cdCoverArt = new JLabel();



        searchButton = new JButton("Search");
        lyricsButton = new JButton("Info");
        pushBoxButton = new JButton("Pushbox");
        searchDatabase = new JTable(searchModel);
        pushDatabase = new JTable(pushModel);
        String[] comboBoxString = { "Artist", "Album", "Title"};
        choices = new JComboBox(comboBoxString);
        artistDatabase = new JList(artistModel);
        albumDatabase = new JList(albumModel);
        titleDatabase = new JList(titleModel);

        String[] profaneBoxString = {"Clean","Dirty","Unknown"};
        profaneBox = new JComboBox(profaneBoxString);
        profaneLabel = new JLabel("Profanity: ");
        profaneButton = new JButton("Update");

        titleDatabase.setDragEnabled(true);
        searchDatabase.setDragEnabled(true);
        searchDatabase.enableInputMethods(false);

        showNameLabel = new JLabel("Logging: ");
        showNameBox = new JComboBox();
        back = new JLabel();
        infoFont = new Font("Serif", Font.PLAIN, 18);

        int numLines=0;
        try { BufferedReader in = new BufferedReader(new FileReader("shows.csv"));
            String readIN="";
            while ((readIN = in.readLine()) != null) {
                numLines++;
            }
            in.close();
        } catch (IOException e) { }


        show = new RadioShow[numLines];

        //POPULATING COMBO BOX WITH SHOW NAMES
        try { BufferedReader in = new BufferedReader(new FileReader("shows.csv"));
            String str, showName, showID, djName, djID;
            int commaIndex, oldCommaIndex, iterator=0;
            while ((str = in.readLine()) != null) {

                commaIndex = str.indexOf(',');
                showName = str.substring(0, commaIndex);

                oldCommaIndex = commaIndex;
                commaIndex = str.indexOf(',', oldCommaIndex+1);
                showID = str.substring(oldCommaIndex+1, commaIndex);

                oldCommaIndex = commaIndex;
                commaIndex = str.indexOf(',', oldCommaIndex+1);
                djName = str.substring(oldCommaIndex+1, commaIndex);

                djID = str.substring(commaIndex+1, str.length());

                show[iterator] = new RadioShow(showName, showID, djName, djID);
                iterator++;
            }
            in.close();
        } catch (IOException e) { }

        for(int i = 0; i< numLines; i++){
            showNameBox.addItem(show[i].getShowName());
        }



        //Load Previous preferences from file (MySQL Path, USER, PASS)
        FileInputStream fin;

        try
        {
            fin = new FileInputStream ("preferences");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            mysqlPath = reader.readLine();
            System.out.println(mysqlPath);
            mysqlUser = reader.readLine();
            System.out.println(mysqlUser);
            mysqlPass = reader.readLine();
            System.out.println(mysqlPass);
            loggingOn = reader.readLine();
            fin.close();
        }catch(Exception e){	//defualts
            mysqlPath = "localhost/music";
            mysqlUser = "root";
            mysqlPass = "";
        }
        try{
            getAllArtists();
        }catch(Exception e){
            e.printStackTrace();
        }


        artistDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        albumDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        titleDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.setTitle("F-Radio");
        loadPushBox();

        JScrollPane artistScrollPane = new JScrollPane(artistDatabase);
        JScrollPane albumScrollPane = new JScrollPane(albumDatabase);
        JScrollPane titleScrollPane = new JScrollPane(titleDatabase);
        JScrollPane searchScrollPane = new JScrollPane(searchDatabase);

        artistScrollPane.setLocation(330,0);
        artistScrollPane.setSize(200,200);
        artistScrollPane.setBorder(BorderFactory.createTitledBorder("Artist"));
        artistScrollPane.setBackground(Color.decode("#6B7552"));

        albumScrollPane.setLocation(537,0);
        albumScrollPane.setSize(200,200);
        albumScrollPane.setBorder(BorderFactory.createTitledBorder("Album"));
        albumScrollPane.setBackground(Color.decode("#6B7552"));

        titleScrollPane.setLocation(330,200);
        titleScrollPane.setSize(410,200);
        titleScrollPane.setBorder(BorderFactory.createTitledBorder("Title"));
        titleScrollPane.setBackground(Color.decode("#6B7552"));

        searchScrollPane.setLocation(330,430);
        searchScrollPane.setSize(410,200);
        searchScrollPane.setBorder(BorderFactory.createTitledBorder("Search Result"));
        searchScrollPane.setBackground(Color.decode("#6B7552"));

        back.setLocation(745, 0);
        back.setSize(450,750);
        back.setBackground(Color.decode("#524D4A"));
        back.setForeground(Color.decode("#524D4A"));
        back.setBorder(BorderFactory.createRaisedBevelBorder());
        back.setOpaque(true);


        artistName.setLocation(755,50);
        artistName.setSize(350, 20);
        artistName.setFont(infoFont);
        artistName.setForeground(Color.LIGHT_GRAY);

        albumName.setLocation(755, 80);
        albumName.setSize(350,20);
        albumName.setFont(infoFont);
        albumName.setForeground(Color.LIGHT_GRAY);

        labelName.setLocation(755, 110);
        labelName.setSize(350,20);
        labelName.setFont(infoFont);
        labelName.setForeground(Color.LIGHT_GRAY);

        titleName.setLocation(755, 140);
        titleName.setSize(350,20);
        titleName.setFont(infoFont);
        titleName.setForeground(Color.LIGHT_GRAY);

        searchBox.setLocation(330, 400);
        searchBox.setSize(190,25);


        cdCoverArt.setLocation(755, 270);
        cdCoverArt.setSize(428,428);
        cdCoverArt.setHorizontalAlignment(JLabel.CENTER);
        cdCoverArt.setVerticalAlignment(JLabel.CENTER);
        cdCoverArt.setBackground(Color.lightGray);
        cdCoverArt.setOpaque(true);
        cdCoverArt.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.BLACK));

        choices.setSelectedIndex(2);
        choices.setLocation(530, 400);
        choices.setSize(100,25);

        profaneLabel.setLocation(755, 175);
        profaneLabel.setSize(125, 20);
        profaneLabel.setForeground(Color.LIGHT_GRAY);

        profaneBox.setSelectedIndex(2);
        profaneBox.setLocation(815,175);
        profaneBox.setSize(125,25);

        profaneButton.setLocation(940, 175);
        profaneButton.setSize(90,25);

        searchButton.setLocation(640,400);
        searchButton.setSize(100,25);

        showNameLabel.setSize(100,25);
        showNameLabel.setLocation(750,10);
        showNameLabel.setForeground(Color.LIGHT_GRAY);
        showNameBox.setSize(250, 25);
        showNameBox.setLocation(850, 10);

        pushBoxButton.setLocation(330,630);
        pushBoxButton.setSize(110,30);

        panel = new JPanel();
        panel.setBackground(Color.decode("#6B7552"));
        artistDatabase.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                if(artistDatabase.getSelectedIndex()>=0){
                    getAlbumsFromArtist(artistDatabase.getSelectedValue().toString());
                    albumDatabase.setSelectedIndex(0);
                    getTitlesFromAlbumArtist(artistDatabase.getSelectedValue().toString(),albumDatabase.getSelectedValue().toString());
                    titleDatabase.setSelectedIndex(0);
                    artistName.setText("Artist: "+artistDatabase.getSelectedValue().toString());

                }
            }

        });
        albumDatabase.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                if(albumDatabase.getSelectedIndex()>=0){

                    getTitlesFromAlbumArtist(artistDatabase.getSelectedValue().toString(),albumDatabase.getSelectedValue().toString());
                    titleDatabase.setSelectedIndex(0);
                    albumName.setText("Album: "+albumDatabase.getSelectedValue().toString());
                    labelName.setText("Label: "+getLabelFromArtistAlbum(artistDatabase.getSelectedValue().toString(),albumDatabase.getSelectedValue().toString()));
                    String path = getPathFromArtistAlbum(artistDatabase.getSelectedValue().toString(),albumDatabase.getSelectedValue().toString());
                    int lastSlash = path.lastIndexOf("/");
                    path = path.substring(0, lastSlash);
                    path = path + "/cover.jpg";
                    cdCoverArt.setText("Album Art Unavailable");
                    cdCoverArt.setIcon(new ImageIcon(path));
                    if(cdCoverArt.getIcon() != null){
                        cdCoverArt.setText("");


                        cdCoverArt.repaint();



                    }
                }
            }

        });
        titleDatabase.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                setSelectedArtist(getArtist());
                setSelectedTitle(getTitle());
                setSelectedAlbum(getAlbum());
                titleName.setText("Title: "+getSelectedTitle());
                char pro = checkProfane();
                if(pro =='c'){
                    profaneBox.setSelectedIndex(0);
                    profaneBox.setForeground(Color.green);
                }
                else if(pro =='d'){
                    profaneBox.setSelectedIndex(1);
                    profaneBox.setForeground(Color.RED);
                }
                else{
                    profaneBox.setSelectedIndex(2);
                    profaneBox.setForeground(Color.black);
                }
            }
        });

        searchDatabase.addMouseListener(new MouseListener(){

            public void mouseClicked(MouseEvent e) {
                getSelectedSearchArtistTitleAlbum();
            }

            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

        });
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                search(searchBox.getText());
            }
        });

        profaneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateProfane();
            }
        });

        pushBoxButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                displayPushBox();
            }
        });


        searchModel.addColumn("Title");
        searchModel.addColumn("Artist");
        searchModel.addColumn("Album");

        pushModel.addColumn("Title");
        pushModel.addColumn("Artist");
        pushModel.addColumn("Album");

        gui1 = new SingleTrackView(this,1);
        gui2 = new SingleTrackView(this,2);
        gui3 = new SingleTrackView(this,3);
        gui1.setSize(315,235);
        gui2.setSize(315,235);
        gui3.setSize(315,235);
        gui1.setLocation(0, 0);
        gui2.setLocation(0,235);
        gui3.setLocation(0,470);
        gui1.setBorder(BorderFactory.createRaisedBevelBorder());
        gui2.setBorder(BorderFactory.createRaisedBevelBorder());
        gui3.setBorder(BorderFactory.createRaisedBevelBorder());
        gui1.setVisible(true);
        gui2.setVisible(true);
        gui3.setVisible(true);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        file = new JMenu("File");
        menuBar.add(file);
        preference = new JMenuItem("Preferences");
        file.add(preference);
        preference.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setPreferences();
            }
        });

        this.add(gui1);
        this.add(gui2);
        this.add(gui3);


        getContentPane().add(panel);
        panel.add(back);
        panel.add(artistScrollPane);
        panel.add(titleScrollPane);
        panel.add(albumScrollPane);
        panel.add(searchScrollPane);
        panel.add(searchBox);
        panel.add(choices);
        panel.add(searchButton);
        panel.add(pushBoxButton);
        panel.add(artistName);
        panel.add(albumName);
        panel.add(labelName);
        panel.add(titleName);
        panel.add(cdCoverArt);
        panel.add(showNameLabel);
        panel.add(showNameBox);
        panel.add(profaneBox);
        panel.add(profaneLabel);
        panel.add(profaneButton);
        panel.setLayout(null);
        panel.setComponentZOrder(back,18);
        artistDatabase.setSelectedIndex(0);



        gui1.setEnabled(true);
        gui1.setDropTarget(new DropTarget(gui1,gui1));
        gui2.setEnabled(true);
        gui2.setDropTarget(new DropTarget(gui2,gui2));
        gui3.setEnabled(true);
        gui3.setDropTarget(new DropTarget(gui3,gui3));
    }


    public void getSelectedSearchArtistTitleAlbum(){
        setSelectedArtist((String)this.searchModel.getValueAt(this.searchDatabase.getSelectedRow(), 1));
        setSelectedTitle((String)this.searchModel.getValueAt(this.searchDatabase.getSelectedRow(), 0));
        setSelectedAlbum((String)this.searchModel.getValueAt(this.searchDatabase.getSelectedRow(), 2));
    }



    public String getArtist(){
        if(artistDatabase.getSelectedIndex() >=0){
            return artistDatabase.getSelectedValue().toString();
        }else{
            return null;
        }
    }
    public String getTitle(){
        if(titleDatabase.getSelectedIndex() >=0){
            return titleDatabase.getSelectedValue().toString();
        }else{
            return null;
        }
    }
    public String getAlbum(){
        return albumDatabase.getSelectedValue().toString();
    }

    public void updateProfane(){
        Connection connect = null;
        String mysqlStatement = "";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager
                    .getConnection("jdbc:mysql://"+mysqlPath+"?"
                            + "user="+mysqlUser+"&password="+mysqlPass);

            if(profaneBox.getSelectedIndex()==0){
                mysqlStatement ="update music set profane='c' where artist='"+getArtist().replaceAll("'", "\\\\'")+"' and album='"+getAlbum().replaceAll("'", "\\\\'")+"' and title='"+getTitle().replaceAll("'", "\\\\'")+"'";
            }
            else if(profaneBox.getSelectedIndex()==1){
                mysqlStatement ="update music set profane='d' where artist='"+getArtist().replaceAll("'", "\\\\'")+"' and album='"+getAlbum().replaceAll("'", "\\\\'")+"' and title='"+getTitle().replaceAll("'", "\\\\'")+"'";
            }
            else if(profaneBox.getSelectedIndex()==2){
                mysqlStatement ="update music set profane='u' where artist='"+getArtist().replaceAll("'", "\\\\'")+"' and album='"+getAlbum().replaceAll("'", "\\\\'")+"' and title='"+getTitle().replaceAll("'", "\\\\'")+"'";
            }
            PreparedStatement statement = connect.prepareStatement(mysqlStatement);
            System.out.println(mysqlStatement);

            statement.execute();


        }catch(Exception e){JOptionPane.showMessageDialog(this, "Database Error. Cannot Update Profanity.");e.printStackTrace();}
    }

    public char checkProfane(){
        char check= 'u';
        Connection connect = null;
        ResultSet resultSet = null;
        String mysqlStatement = "SELECT profane from music where artist=\""+getArtist()+"\" and album=\""+getAlbum()+"\" and title=\""+getTitle()+"\"";
        mysqlStatement.replace("'", "\'");
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager
                    .getConnection("jdbc:mysql://"+mysqlPath+"?"
                            + "user="+mysqlUser+"&password="+mysqlPass);
            PreparedStatement statement = connect.prepareStatement(mysqlStatement);
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getString("profane").length() > 0){
                    check = resultSet.getString("profane").charAt(0);
                }
                else{
                    check='u';
                }
            }
        }catch(Exception e){JOptionPane.showMessageDialog(this, "Database Error. Cannot Get Profanity.");e.printStackTrace();}

        return check;
    }

    public void getAllArtists(){
        ResultSet resultSet = null;
        resultSet = getDBInfo("SELECT DISTINCT artist from music ORDER BY artist");
        if(resultSet != null){
            try {
                while(resultSet.next()){
                    artistModel.addElement(resultSet.getString("artist"));
                }
            }catch(SQLException ex){JOptionPane.showMessageDialog(this, "Database Error. Cannot Get All Artists"); ex.printStackTrace();}
        }

    }
    public void getAlbumsFromArtist(String artist){
        ResultSet resultSet = null;
        resultSet = getDBInfo("SELECT DISTINCT album from music WHERE artist=\""+artist.replaceAll("'", "\\\\'")+"\"");
        albumModel.removeAllElements();
        try {
            int i=0;
            while(resultSet.next()){
                albumModel.addElement(resultSet.getString("album"));
                i++;
            }
        }catch(SQLException ex){JOptionPane.showMessageDialog(this, "Database Error. Can't Get Albums From Artists"); ex.printStackTrace();}

    }
    public void getTitlesFromAlbumArtist(String artist, String album){
        ResultSet resultSet = null;
        resultSet = getDBInfo("SELECT title from music WHERE artist=\""+artist.replaceAll("'", "\\\\'")+"\" and album=\""+album.replaceAll("'", "\\\\'")+"\"");
        titleModel.removeAllElements();
        int i=0;
        try{
            while(resultSet.next()){
                titleModel.addElement(resultSet.getString("title"));
                i++;
            }

        }catch(SQLException e){JOptionPane.showMessageDialog(this, "Database Error. Can't Get Titles from Album/Artist");e.printStackTrace();}
    }

    public String getLabelFromArtistAlbum(String artist, String album){
        ResultSet resultSet = null;
        resultSet = getDBInfo("SELECT label from music WHERE artist=\""+artist.replaceAll("'", "\\\\'")+"\" and album=\""+album.replaceAll("'", "\\\\'")+"\"");
        try {
            resultSet.next();
            return resultSet.getString("label");
        } catch (SQLException e) {
            e.printStackTrace();
            return "no label";
        }
    }
    public String getPathFromArtistAlbum(String artist, String album){
        ResultSet resultSet = null;
        resultSet = getDBInfo("SELECT path from music WHERE artist=\""+artist.replaceAll("'", "\\\\'")+"\" and album=\""+album.replaceAll("'", "\\\\'")+"\"");
        try {
            resultSet.next();
            return resultSet.getString("path");
        } catch (SQLException e) {
            e.printStackTrace();
            return "no path";
        }
    }


    public ResultSet getDBInfo(String mysqlStatement){
        Connection connect = null;
        ResultSet resultSet = null;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connect = DriverManager
                    .getConnection("jdbc:mysql://"+mysqlPath+"?"
                            + "user="+mysqlUser+"&password="+mysqlPass);
            PreparedStatement statement = connect.prepareStatement(mysqlStatement);
            resultSet = statement.executeQuery();
        }catch(Exception e){JOptionPane.showMessageDialog(this, "Database Error. Cannot Get Information from the Database.");e.printStackTrace();}
        return resultSet;
    }
    public void setSelectedArtist(String artist){
        selectedArtist = artist;

    }
    public void setSelectedAlbum(String album){
        selectedAlbum = album;

    }
    public void setSelectedTitle(String title){
        selectedTitle = title;
    }
    public String getSelectedTitle(){

        return selectedTitle;
    }
    public String getSelectedAlbum(){
        return selectedAlbum;
    }
    public String getSelectedArtist(){

        return selectedArtist;
    }
    public void makeThread(int playerID,Runnable run){
        if(playerID == 1){
            t1 = new Thread(run);
            t1.start();
        }
        if(playerID == 2){
            t2 = new Thread(run);
            t2.start();
        }
        if(playerID == 3){
            t3 = new Thread(run);
            t3.start();
        }
    }
    public void search(String searchField){
        ResultSet results=null;
        int i=0;
        while(searchModel.getRowCount()>0){
            searchModel.removeRow(i);
        }
        if(choices.getSelectedItem().equals("Title")){
            results = getDBInfo("SELECT DISTINCT title, artist, album from music WHERE title like \"%"+searchField+"%\"");
        }else if(choices.getSelectedItem().equals("Artist")){
            results = getDBInfo("SELECT DISTINCT title, artist, album from music WHERE artist like \"%"+searchField+"%\"");
        }
        else if(choices.getSelectedItem().equals("Album")){
            results = getDBInfo("SELECT DISTINCT title, artist, album from music WHERE album like \"%"+searchField+"%\"");
        }
        try {
            String title, artist, album;
            while(results.next()){
                title = results.getString("title");
                artist = results.getString("artist");
                album = results.getString("album");
                searchModel.addRow(new String[]{title,artist,album});
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setPreferences(){
        JFrame prefs = new JFrame();
        prefs.setSize(500,250);
        prefs.setVisible(true);

        locationField = new JTextField(mysqlPath);
        userNameField = new JTextField(mysqlUser);
        passwordField = new JTextField();
        logging = new JComboBox();
        logging.addItem("ON");
        logging.addItem("OFF");
        if(loggingOn.equals("ON")){
            logging.setSelectedIndex(0);
        }else{
            logging.setSelectedIndex(1);
        }
        JLabel locationLabel = new JLabel("Location to MySql Database: ");
        JLabel userNameLabel = new JLabel("MySql Username: ");
        JLabel passwordLabel = new JLabel("MySql Password: ");
        JLabel loggingLabel = new JLabel("Auto-Logging Songs is set to: ");
        JButton updateButton = new JButton("Update");

        locationField.setSize(150, 20);
        userNameField.setSize(150, 20);
        passwordField.setSize(150, 20);
        logging.setSize(150,20);
        updateButton.setSize(150, 55);

        locationLabel.setSize(250, 20);
        userNameLabel.setSize(250, 20);
        passwordLabel.setSize(250, 20);
        loggingLabel.setSize(250, 20);

        locationField.setLocation(300,20);
        userNameField.setLocation(300,50);
        passwordField.setLocation(300,80);
        logging.setLocation(300,110);
        updateButton.setLocation(300, 140);

        locationLabel.setLocation(20,20);
        userNameLabel.setLocation(20,50);
        passwordLabel.setLocation(20,80);
        loggingLabel.setLocation(20,110);

        prefs.add(locationLabel);
        prefs.add(userNameLabel);
        prefs.add(passwordLabel);
        prefs.add(loggingLabel);

        prefs.add(locationField);
        prefs.add(userNameField);
        prefs.add(passwordField);
        prefs.add(logging);
        prefs.add(updateButton);
        prefs.setLayout(null);
        prefs.setVisible(true);
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updatePrefs();
            }
        });


    }
    public void updatePrefs(){
        FileOutputStream fout;
        String path;
        String user;
        String pass;
        String logYN;
        path = locationField.getText();
        user = userNameField.getText();
        pass = passwordField.getText();
        logYN = logging.getSelectedItem().toString();
        loggingOn = logYN;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connect = DriverManager
                    .getConnection("jdbc:mysql://"+path+"?"
                            +"user="+user+"&password="+pass);
            connect.close();
            fout = new FileOutputStream ("preferences");
            new PrintStream(fout).println (path);//path
            new PrintStream(fout).println (user);//user
            new PrintStream(fout).println (pass);//password
            new PrintStream(fout).println (logYN);//logging ON/OFF
            fout.close();
        }
        catch (IOException e)
        {
            System.err.println ("Unable to write to file");
        }catch (Exception e){}
    }

    public void loadPushBox(){


        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connect = DriverManager
                    .getConnection("jdbc:mysql://"+mysqlPath+"?"
                            +"user="+mysqlUser+"&password="+mysqlPass);
            PreparedStatement statement = connect.prepareStatement("TRUNCATE TABLE pushbox");
            statement.execute();
            connect.close();
        }catch(Exception e){JOptionPane.showMessageDialog(this, "Database Error. Cannot Load Pushbox.");e.printStackTrace();}
        //System.out.println("Hello There Fella!");
        Thread pushy = new Thread(this);
        pushy.start();

    }
    public void getPushboxArtistTitleAlbum(){
        setSelectedArtist((String)this.pushModel.getValueAt(pushDatabase.getSelectedRow(), 1));
        setSelectedTitle((String)this.pushModel.getValueAt(pushDatabase.getSelectedRow(), 0));
        setSelectedAlbum((String)this.pushModel.getValueAt(pushDatabase.getSelectedRow(), 2));
    }

    public void logSong(String passArtist, String passAlbum, String passTitle){

        String logArtist,logAlbum,logTitle,logLabel;
        logArtist = passArtist.replace(',', ' ');
        logAlbum = passAlbum.replace(',', ' ');
        logTitle = passTitle.replace(',', ' ');
        logLabel = "N/A";
        String DATE_FORMAT_NOW = "yyyy-MM-dd ";
        String TIME_FORMAT_NOW = "HH:mm:ss";

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_NOW);
        SimpleDateFormat sdf2 = new SimpleDateFormat(TIME_FORMAT_NOW);
        String date = sdf1.format(cal.getTime());
        String time = sdf2.format(cal.getTime());

        String[] monthName = {"January", "February","March", "April", "May", "June", "July","August", "September", "October", "November","December"};

        String month = monthName[cal.get(Calendar.MONTH)];

        ResultSet resultSet = null;
        resultSet = getDBInfo("SELECT label from music WHERE artist=\""+logArtist+"\" and album=\""+logAlbum+"\" and title=\""+logTitle+"\"");

        try{

            while(resultSet.next()){
                logLabel = resultSet.getString("label");
                logLabel = logLabel.replace(',', ' ');
            }

        }catch(SQLException e){JOptionPane.showMessageDialog(this, "Cannot Find Label.");}
        if(logLabel == null){
            logLabel = "Not Found";
        }
        if(loggingOn.equals("ON")){
            try
            {
                //"Date","Time","ShowID","Artist","Title","Album","Label","DJ_ID"
                String entry = "\""+date+"\",\""+time+"\",\""+show[showNameBox.getSelectedIndex()].getShowID()+"\",\""+logArtist+"\",\""+logTitle+"\",\""+logAlbum+"\",\""+logLabel+"\",\""+show[showNameBox.getSelectedIndex()].getDjID()+"\",\"\",\"\",\"\",\"\",\"-1\",\"\",\"\",\"\",\"\",\"\"";
                //Testing
                //BufferedWriter bw = new BufferedWriter (new FileWriter (month+".csv", true));
                BufferedWriter bw = new BufferedWriter (new FileWriter ("\\\\WJCUENCODER\\FlacRadio_logs\\"+month+".csv", true));
                bw.write (entry);
                bw.newLine();
                bw.flush();
                bw.close();
            }
            // Catches any error conditions
            catch (IOException e)
            {
                System.err.println ("Unable to write to file");
            }
        }
    }

    public static void main(String[] args) {
        FlacItRadioMain db = new FlacItRadioMain();
        db.setTitle("F-Radio.");
        db.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        db.setVisible(true);
    }

    public void displayPushBox(){
        ResultSet resultSet = null;
        resultSet = getDBInfo("SELECT DISTINCT artist,album,title from pushbox");

        try {
            String title, artist, album;
            while(resultSet.next()){
                title = resultSet.getString("title");
                artist = resultSet.getString("artist");
                album = resultSet.getString("album");
                searchModel.addRow(new String[]{title,artist,album});
                System.out.println(searchModel.getRowCount());
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run() {
        // TODO Auto-generated method stub
        System.out.println("Getting Pushbox");
        int i = 0;
        while(searchModel.getRowCount()>0){
            searchModel.removeRow(i);
        }

        //MetaTest scanner = new MetaTest("\\\\Wjcu-genre-1\\Pushbox\\");

        //Testing Directory
        //MetaTest scanner = new MetaTest("/Users/markryan/Desktop/FLAC");
        //Thread getPushy = new Thread(scanner);
        //getPushy.start();
        //scanner.run(scanner.GetPushDir());

    }


}