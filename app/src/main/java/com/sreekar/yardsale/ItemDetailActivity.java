package com.sreekar.yardsale;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreekar.yardsale.models.Item;
import com.sreekar.yardsale.viewholder.ItemViewHolder;

public class ItemDetailActivity extends BaseActivity {

    private static final String TAG = "ItemDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mItemReference;
    private ValueEventListener mItemListener;

    private String itemKey;

    private TextView title;
    private TextView seller;
    private TextView price;
    private TextView description;
    private ImageView image;
    private RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Get post key from intent
        itemKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (itemKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mItemReference = FirebaseDatabase.getInstance().getReference()
                .child("items").child(itemKey);

        // Initialize Views
        title = (TextView) findViewById(R.id.tvtitle);
        seller = (TextView) findViewById(R.id.tvseller);
        price = (TextView) findViewById(R.id.tvprice);
        description = (TextView) findViewById(R.id.tvdescription);
        image = (ImageView) findViewById(R.id.item_image);
        rating = (RatingBar) findViewById(R.id.ratingBar);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Item item = dataSnapshot.getValue(Item.class);

                title.setText(item.getTitle());
                seller.setText(item.getSellerName());
                price.setText(item.getPrice().toString());
                description.setText(item.getDescription());
                rating.setRating(item.getCondition());
                image.setImageBitmap(ItemViewHolder.decodeImage(item.getImage()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                Toast.makeText(ItemDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mItemReference.addValueEventListener(itemListener);

        // Keep copy of post listener so we can remove it when app stops
        mItemListener = itemListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mItemListener != null) {
            mItemReference.removeEventListener(mItemListener);
        }
    }
}
