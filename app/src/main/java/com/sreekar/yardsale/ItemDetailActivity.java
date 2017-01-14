package com.sreekar.yardsale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sreekar.yardsale.models.Comment;
import com.sreekar.yardsale.models.Item;
import com.sreekar.yardsale.models.User;
import com.sreekar.yardsale.viewholder.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/*
This activity is shown after a user clicks on an item in the main page. It shows the name
of the selected item, the seller, price, rating of the condition, an image of the item,
a description, a button to buy the item, and a comments section where users can post comments.
 */

public class ItemDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ItemDetailActivity";

    public static final String EXTRA_ITEM_KEY = "item_key";

    private DatabaseReference mItemReference;
    private ValueEventListener mItemListener;

    private DatabaseReference mCommentsReference;
    private CommentAdapter mAdapter;

    private Context mContext;
    private DatabaseReference mDatabaseReference;
    
    private String itemKey;

    private TextView title;
    private TextView seller;
    private TextView price;
    private TextView description;
    private ImageView image;
    private RatingBar rating;

    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;

    private Button buy;

    //Initialize donate button and set a listener on the button
    public void buy(){
        buy = (Button) findViewById(R.id.button_buy);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent buyIntent = new Intent(ItemDetailActivity.this, BuyActivity.class);
                buyIntent.putExtra(ItemDetailActivity.EXTRA_ITEM_KEY, itemKey);

                startActivity(buyIntent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        buy();

        // Get item key from intent
        itemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);
        if (itemKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_ITEM_KEY");
        }

        // Initialize Database
        mItemReference = FirebaseDatabase.getInstance().getReference()
                .child("items").child(itemKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(itemKey);

        // Initialize Views
        title = (TextView) findViewById(R.id.tvtitle);
        seller = (TextView) findViewById(R.id.tvseller);
        price = (TextView) findViewById(R.id.tvprice);
        description = (TextView) findViewById(R.id.tvdescription);
        image = (ImageView) findViewById(R.id.item_image);
        rating = (RatingBar) findViewById(R.id.ratingBar);

        mCommentField = (EditText) findViewById(R.id.field_comment_text);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);

        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        buy = (Button) findViewById(R.id.button_buy);
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

                //Setting text
                title.setText(item.getTitle());
                seller.setText(item.getSellerName());
                //Formatting price with $ before the number and 2 places after the decimal
                String strPrice = String.format("$%.2f", item.getPrice());
                price.setText(strPrice);
                description.setText(item.getDescription());
                rating.setRating(item.getCondition());
                image.setImageBitmap(ItemViewHolder.decodeImage(item.getImage()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                Toast.makeText(ItemDetailActivity.this, "Failed to load item.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mItemReference.addValueEventListener(itemListener);

        // Keep copy of post listener so we can remove it when app stops
        mItemListener = itemListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mItemListener != null) {
            mItemReference.removeEventListener(mItemListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new comment object
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = (TextView) itemView.findViewById(R.id.comment_author);
            bodyView = (TextView) itemView.findViewById(R.id.comment_body);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }
    }
}
