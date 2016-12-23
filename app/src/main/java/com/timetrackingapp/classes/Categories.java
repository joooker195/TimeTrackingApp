package com.timetrackingapp.classes;

/**
 * Created by Ксю on 23.12.2016.
 */
public class Categories
{
    private int Id;
    private String CategoryName;

    public Categories(int id, String categoryName) {
        CategoryName = categoryName;
        Id = id;
    }

    public Categories(String categoryName) {
        CategoryName = categoryName;
    }

    public  Categories(){}

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    @Override
    public String toString() {
        return CategoryName;
    }
}
