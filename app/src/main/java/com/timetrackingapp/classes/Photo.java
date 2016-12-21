package com.timetrackingapp.classes;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Ксю on 21.12.2016.
 */
public class Photo implements Serializable
{
    private int id;
    private String name;
    private Bitmap image;

    public Photo(int id, Bitmap image) {
        this.id = id;
        this.image = image;
    }

    public Photo() {

    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

}
