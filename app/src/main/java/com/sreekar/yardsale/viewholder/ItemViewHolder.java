package com.sreekar.yardsale.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sreekar.yardsale.R;
import com.sreekar.yardsale.models.Item;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView priceView;

    public ItemViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.item_title);
        priceView = (TextView) itemView.findViewById(R.id.tvprice);
    }

    public void bindToItem(Item item) {
        titleView.setText(item.getTitle());
        priceView.setText(item.getPrice().toString());
    }
}
