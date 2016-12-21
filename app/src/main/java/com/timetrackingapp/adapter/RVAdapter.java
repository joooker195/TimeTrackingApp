package com.timetrackingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timetrackingapp.R;
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
        final View mVewCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_row, parent, false);
        CategoryViewHolder mCategoryViewHolder = new CategoryViewHolder(mVewCard);

        return mCategoryViewHolder;
    }

    @Override
    public void onBindViewHolder(RVAdapter.CategoryViewHolder holder, int position) {
        holder.mTitle.setText(categories.get(position).getTitle());
        holder.mDesc.setText(categories.get(position).getDesc());
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
            mDesc = (TextView)itemView.findViewById(R.id.desc_row_category);
        }
    }
}
