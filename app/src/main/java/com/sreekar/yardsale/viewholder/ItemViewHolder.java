package com.sreekar.yardsale.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sreekar.yardsale.R;
import com.sreekar.yardsale.models.Item;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView priceView;
    public ImageView itemImage;

    public ItemViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.item_title);
        priceView = (TextView) itemView.findViewById(R.id.tvprice);
        itemImage = (ImageView) itemView.findViewById(R.id.item_photo);
    }

    public void bindToItem(Item item) {
        titleView.setText(item.getTitle());
        priceView.setText(item.getPrice().toString());
        itemImage.setImageBitmap(decodeImage(item.getImage()));
    }

    public  static Bitmap decodeImage(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray,0, decodedByteArray.length);
    }
}
