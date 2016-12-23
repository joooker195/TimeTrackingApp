package com.timetrackingapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.timetrackingapp.R;
import com.timetrackingapp.classes.Photo;

import java.util.List;

/**
 * Created by Ксю on 23.12.2016.
 */
public class CustomPhotoAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Photo tempValues;
    private List<Photo> data;
    private Context ctx;
    private  int LayResId;

    public CustomPhotoAdapter(Context context, int resource, List<Photo> objects) {
       // super(context, resource, objects);
        this.ctx = context;
        this.LayResId = resource;
        this.data = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public Photo getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(LayResId, parent, false);
        tempValues =  getRecord(position);
        ImageView container = (ImageView) row.findViewById(R.id.bmp);
        container.setImageBitmap(tempValues.getImage());
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position,convertView,parent);
    }

    public Photo getRecord(int Position){
        return  getItem(Position);
    }

    @Override
    public int getCount() {
        return data.size();
    }
}
