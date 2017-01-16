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

import static com.sreekar.yardsale.utils.ImageUtils.decodeImage;

/*
This class inputs the information into the item model.
 */
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
        String strPrice = String.format("$%.2f", item.getPrice());
        priceView.setText(strPrice);
        itemImage.setImageBitmap(decodeImage(item.getImage()));
    }

}
