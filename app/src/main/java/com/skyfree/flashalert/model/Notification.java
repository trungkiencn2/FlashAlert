package com.skyfree.flashalert.model;

import java.io.Serializable;

/**
 * Created by KienBeu on 5/16/2018.
 */

public class Notification implements Serializable{
    private String pack, title, text;

    public Notification(String pack, String title, String text) {
        this.pack = pack;
        this.title = title;
        this.text = text;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Notification() {

    }
}
