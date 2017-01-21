package com.sreekar.yardsale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreekar.yardsale.adapter.CommentAdapter;
import com.sreekar.yardsale.models.Comment;
import com.sreekar.yardsale.models.Item;
import com.sreekar.yardsale.models.User;
import com.sreekar.yardsale.viewholder.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.sreekar.yardsale.utils.ImageUtils.decodeImage;

/**
 * This activity is shown after a user clicks on an item in the main page. It shows the name
 * of the selected item, the seller, price, rating of the condition, an image of the item,
 * a description, a button to buyButton the item, and a comments section where users can
 * post comments.
 */
public class ItemDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ItemDetailActivity";
    public static final String EXTRA_ITEM_KEY = "item_key";

    private DatabaseReference itemReference;
    private DatabaseReference commentsReference;

    private String itemKey;

    private TextView title;
    private TextView seller;
    private TextView price;
    private TextView description;
    private ImageView image;
    private RatingBar rating;
    private TextView purchased;

    private Button buyButton;

    private EditText commentField;
    private Button commentButton;

    private CommentAdapter commentAdapter;
    private RecyclerView commentsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Get item key from intent
        itemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);
        if (itemKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_ITEM_KEY");
        }

        // Initialize Database
        itemReference = FirebaseDatabase.getInstance().getReference().child("items").child(itemKey);
        commentsReference = FirebaseDatabase.getInstance().getReference().child("post-comments").child(itemKey);

        // Initialize Views
        title = (TextView) findViewById(R.id.tvtitle);
        purchased = (TextView) findViewById(R.id.purchased) ;
        seller = (TextView) findViewById(R.id.tvseller);
        price = (TextView) findViewById(R.id.tvprice);
        description = (TextView) findViewById(R.id.tvdescription);
        image = (ImageView) findViewById(R.id.item_image);
        rating = (RatingBar) findViewById(R.id.ratingBar);

        buyButton = (Button) findViewById(R.id.button_buy);
        buyButton.setOnClickListener(this);

        commentField = (EditText) findViewById(R.id.field_comment_text);
        commentButton = (Button) findViewById(R.id.button_post_comment);
        commentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);

        commentButton.setOnClickListener(this);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the item
        itemReference.addListenerForSingleValueEvent(new ItemDetailValueEventListener());

        // Listen for comments
        commentAdapter = new CommentAdapter(this, commentsReference);
        commentsRecycler.setAdapter(commentAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Clean up comments listener
        commentAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        } else if (i == R.id.button_buy) {
            buy();
        }
    }

    // Start the buy activity
    private void buy() {
        Intent buyIntent = new Intent(ItemDetailActivity.this, BuyActivity.class);
        // pass the item key to the buy activity
        buyIntent.putExtra(ItemDetailActivity.EXTRA_ITEM_KEY, itemKey);

        startActivity(buyIntent);
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new CommentValueEventListener(uid));
    }

    /**
     * ValueEventListener for loading item details from database and setting the view
     */
    private class ItemDetailValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Item item = dataSnapshot.getValue(Item.class);

            //Setting item details
            title.setText(item.getTitle());
            seller.setText(item.getSellerName());
            //Formatting price with $ before the number and 2 places after the decimal
            String strPrice = String.format("$%.2f", item.getPrice());
            price.setText(strPrice);
            description.setText(item.getDescription());
            rating.setRating(item.getCondition());
            image.setImageBitmap(decodeImage(item.getImage()));

            if(item.isPurchased() == true){
                buyButton.setEnabled(false);
                purchased.setText("This item has been sold");
            } else {
                purchased.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "itemDetail:onCancelled", databaseError.toException());
            Toast.makeText(ItemDetailActivity.this, "Failed to load item.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ValueEventListenr for pushing comment to the database
     */
    private class CommentValueEventListener implements ValueEventListener {
        private String uid;

        CommentValueEventListener(String uid) {
            this.uid = uid;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            // make sure all the fields are set
            if (!validateForm()) {
                return;
            }

            // Get user information
            User user = dataSnapshot.getValue(User.class);
            String authorName = user.username;

            // Create new comment object
            String commentText = commentField.getText().toString();
            Comment comment = new Comment(uid, authorName, commentText);

            // Push the comment, it will appear in the list
            commentsReference.push().setValue(comment);

            // Clear the field
            commentField.setText(null);
        }

        /**
         * Validates that all the fields are set, returns false if any of the required fields is not set.
         */
        private boolean validateForm() {
            boolean result = true;

            commentField.setError(null);

            if (TextUtils.isEmpty(commentField.getText().toString())) {
                commentField.setError("Required");
                result = false;
            }

            return result;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "commentValue:onCancelled", databaseError.toException());
            Toast.makeText(ItemDetailActivity.this, "Failed to load comment.", Toast.LENGTH_SHORT).show();
        }
    }
}
