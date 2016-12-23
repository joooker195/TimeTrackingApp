package com.timetrackingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.timetrackingapp.R;
import com.timetrackingapp.classes.Category;

import java.util.List;

/**
 * Created by Ксю on 23.12.2016.
 */
public class StatisticAdapter extends BaseAdapter
{
    private List<Category> data;
    private Context ctx;
    private  int LayResId;
    private LayoutInflater inflater;

    public StatisticAdapter(List<Category> data, Context ctx, int layResId) {
        this.data = data;
        this.ctx = ctx;
        LayResId = layResId;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = inflater.inflate(LayResId,viewGroup,false);
        Category currEntity = getEntity(i);

        TextView monthName = (TextView) row.findViewById(R.id.category_statistic);
        monthName.setText(currEntity.getTitle());
        return row;
    }
    public Category getEntity(int Position){
        return (Category) getItem(Position);
    }
}
