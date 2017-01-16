package com.sreekar.yardsale.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.sreekar.yardsale.R;
import com.sreekar.yardsale.models.Comment;
import com.sreekar.yardsale.viewholder.CommentViewHolder;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private static final String TAG = "CommentAdapter";

    private Context context;
    private DatabaseReference commentsReference;
    private ChildEventListener childEventListener;

    private List<Comment> comments = new ArrayList<>();

    public CommentAdapter(final Context context, DatabaseReference ref) {
        this.context = context;
        this.commentsReference = ref;

        // Create child event listener and store reference to listener so it can be removed on app stop
        this.childEventListener = new CommentsChildEventListener();
        ref.addChildEventListener(childEventListener);
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.authorView.setText(comment.author);
        holder.bodyView.setText(comment.text);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void cleanupListener() {
        if (childEventListener != null) {
            commentsReference.removeEventListener(childEventListener);
        }
    }

    /**
     * ChildEventListener for comments
     */
    private class CommentsChildEventListener implements ChildEventListener {
        private List<String> commentIds = new ArrayList<>();

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

            // A new comment has been added, add it to the displayed list
            Comment comment = dataSnapshot.getValue(Comment.class);

            // Update RecyclerView
            commentIds.add(dataSnapshot.getKey());
            comments.add(comment);
            notifyItemInserted(comments.size() - 1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            Comment newComment = dataSnapshot.getValue(Comment.class);
            String commentKey = dataSnapshot.getKey();

            int commentIndex = commentIds.indexOf(commentKey);
            if (commentIndex > -1) {
                // Replace with the new data
                comments.set(commentIndex, newComment);

                // Update the RecyclerView
                notifyItemChanged(commentIndex);
            } else {
                Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so remove it.
            String commentKey = dataSnapshot.getKey();

            int commentIndex = commentIds.indexOf(commentKey);
            if (commentIndex > -1) {
                // Remove data from the list
                commentIds.remove(commentIndex);
                comments.remove(commentIndex);

                // Update the RecyclerView
                notifyItemRemoved(commentIndex);
            } else {
                Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            Toast.makeText(CommentAdapter.this.context, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

