package com.timetrackingapp.classes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ксю on 21.12.2016.
 */
public class Record implements Serializable
{
    private int id;
    private String desc;
    private long interval;
    private long begin;
    private long end;
    private Category category;
    private List<Photo> photos;

    public Record() {
    }

    public Record(int id, String desc, long interval, long begin, long end, Category category, List<Photo> photos) {
        this.id = id;
        this.desc = desc;
        this.interval = interval;
        this.begin = begin;
        this.end = end;
        this.category = category;
        this.photos = photos;
    }

    public Record(String desc) {
        this.desc = desc;
    }

    public Record(String desc, long interval, long begin, long end, Category category, List<Photo> photos) {
        this.desc = desc;
        this.interval = interval;
        this.begin = begin;
        this.end = end;
        this.category = category;
        this.photos = photos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
