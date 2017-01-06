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
    private int categoryRef;
    private List<Photo> photos;
    private List<String> photosId;
    private String categoryTitle;

    public int getCategoryRef() {
        return categoryRef;
    }

    public void setCategoryRef(int categoryRef) {
        this.categoryRef = categoryRef;
    }


    public Record() {
    }

    public Record(String desc, long interval, long begin, long end, int categoryRef) {
        this.desc = desc;
        this.interval = interval;
        this.begin = begin;
        this.end = end;
        this.categoryRef = categoryRef;
    }

    public Record(String desc, long interval, long begin, long end, int category, String categoryTitle) {
        this.desc = desc;
        this.interval = interval;
        this.begin = begin;
        this.end = end;
        this.categoryRef = category;
        this.categoryTitle = categoryTitle;
    }

    public Record(String desc) {
        this.desc = desc;
    }

    public List<String> getPhotosId() {
        return photosId;
    }

    public void setPhotosId(List<String> photosId) {
        this.photosId = photosId;
    }

    public Record(String desc, long interval, long begin, long end, int category, String categoryTitle, List<Photo> photos) {
        this.desc = desc;
        this.interval = interval;
        this.begin = begin;
        this.end = end;
        this.categoryRef = category;
        this.photos = photos;
        this.categoryTitle = categoryTitle;

    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
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

    public int getCategory() {
        return categoryRef;
    }

    public void setCategory(int category) {
        this.categoryRef = category;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
