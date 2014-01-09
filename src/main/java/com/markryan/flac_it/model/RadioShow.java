package com.markryan.flac_it.model;

/**
 * Created by markryan on 1/8/14.
 */
public class RadioShow {
    String showName, showID, djName, djID;

    public RadioShow(String sName, String sID, String dName, String dID){
        showName = sName;
        showID = sID;
        djName = dName;
        djID = dID;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowID() {
        return showID;
    }

    public void setShowID(String showID) {
        this.showID = showID;
    }

    public String getDjName() {
        return djName;
    }

    public void setDjName(String djName) {
        this.djName = djName;
    }

    public String getDjID() {
        return djID;
    }

    public void setDjID(String djID) {
        this.djID = djID;
    }

}
