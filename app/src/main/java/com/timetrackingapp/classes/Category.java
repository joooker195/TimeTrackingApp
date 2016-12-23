package com.timetrackingapp.classes;

import java.io.Serializable;

/**
 * Created by Ксю on 21.12.2016.
 */
public class Category implements Serializable
{
    private int id;
    private String title;
    private String desc;

    public Category() {

    }

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Category(String title) {
        this.title = title;
     //   this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
