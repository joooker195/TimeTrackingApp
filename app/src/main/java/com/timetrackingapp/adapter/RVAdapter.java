package com.timetrackingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timetrackingapp.R;
import com.timetrackingapp.AddRecordActivity;
import com.timetrackingapp.classes.Category;

import java.util.ArrayList;

/**
 * Created by Ксю on 21.12.2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.CategoryViewHolder>
{

    ArrayList<Category> categories = new ArrayList<>();
    Context context;


    public RVAdapter(ArrayList<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }


    @Override
    public RVAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View mVewCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_category_row, parent, false);
        CategoryViewHolder mCategoryViewHolder = new CategoryViewHolder(mVewCard);

        return mCategoryViewHolder;
    }

    @Override
    public void onBindViewHolder(RVAdapter.CategoryViewHolder holder, final int position) {
        holder.mTitle.setText(categories.get(position).getTitle());
      //  holder.mDesc.setText(categories.get(position).getDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRecordActivity.TITLE = categories.get(position).getTitle();

                Intent intent= new Intent(context, AddRecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public  class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView mTitle;
        TextView mDesc;
        public CategoryViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView)itemView.findViewById(R.id.title_row_category);
           // mDesc = (TextView)itemView.findViewById(R.id.desc_row_category);
        }
    }
}
