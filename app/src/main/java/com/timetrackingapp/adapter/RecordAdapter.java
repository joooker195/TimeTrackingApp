package com.timetrackingapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timetrackingapp.R;
import com.timetrackingapp.classes.Photo;
import com.timetrackingapp.classes.Record;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Ксю on 23.12.2016.
 */
public class RecordAdapter extends BaseAdapter {

    private static final String LOG_TAG = "TimeRecordAdapter";
    private List<Record> data;
    private LayoutInflater inflater;
    private Context ctx;
    private  int LayResId;
    private DateFormat dfISO;
    private Date date;


    public RecordAdapter(Context context, int resource, List<Record> objects) {
        this.ctx =context;
        this.LayResId = resource;
        this.data = objects;
        inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dfISO = new SimpleDateFormat("dd.MM.yyyy HH:mm");
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
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View row  = inflater.inflate(LayResId,viewGroup,false);
        Record record = getRecord(i);

        TextView recordDesc = (TextView) row.findViewById(R.id.record_row_desc);
        recordDesc.setText(recordDesc.getText()+record.getDesc());

        TextView startDate = (TextView) row.findViewById(R.id.record_row_begin);
        date = new Date(record.getBegin());
        startDate.setText(startDate.getText() +dfISO.format(date));

        TextView recordCategory = (TextView) row.findViewById(R.id.record_row_cat);
        recordCategory.setText(recordCategory.getText() +record.getCategoryTitle());

        TextView endTime = (TextView) row.findViewById(R.id.record_row_end);
        date = new Date(record.getEnd());
        endTime.setText(endTime.getText() + dfISO.format(date));

        TextView segment = (TextView) row.findViewById(R.id.record_row_interval);
        segment.setText(segment.getText() + String.valueOf(record.getInterval()));

        LinearLayout linearLayout = (LinearLayout) row.findViewById(R.id.photos_layout);
        for (Photo photo:record.getPhotos()){
            try {
                if(photo!=null) {
                    ImageView imageView = new ImageView(ctx);
                    imageView.setImageBitmap(photo.getImage());
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(imageView);
                }
            }
            catch (Exception e)
            {
                Log.e("TRAExc", e.getMessage());
            }
        }
        return row;
    }

    public Record getRecord(int Position){
        return (Record) getItem(Position);
    }
}
