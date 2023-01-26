package com.expert.fire.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.expert.fire.Models.Dishes;
import com.expert.fire.R;

import java.util.List;

public class RecommendationsAdapter extends BaseAdapter {


    Context mContext;
    List<Dishes> dishList;

    public RecommendationsAdapter(Context mContext, List<Dishes> dishList) {
        this.mContext = mContext;
        this.dishList = dishList;
    }

    @Override
    public int getCount() {
        return dishList.size();
    }

    @Override
    public Object getItem(int position) {
        return dishList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_recommend, parent, false);
        TextView txtDishName = mView.findViewById(R.id.txtDishName);
        Dishes dish = dishList.get(position);
        txtDishName.setText(dish.getDish());
        return mView;
    }
}
