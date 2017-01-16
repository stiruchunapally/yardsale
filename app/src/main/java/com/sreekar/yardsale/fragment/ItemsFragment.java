package com.sreekar.yardsale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sreekar.yardsale.ItemDetailActivity;
import com.sreekar.yardsale.R;
import com.sreekar.yardsale.models.Item;
import com.sreekar.yardsale.viewholder.ItemViewHolder;

/*
This is the basic layout for all three fragments,
 */
public abstract class ItemsFragment extends Fragment {
    private DatabaseReference database;

    private RecyclerView recycler;
    private LinearLayoutManager manager;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_items, container, false);
        database = FirebaseDatabase.getInstance().getReference();
        
        recycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        recycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recycler.setLayoutManager(manager);

        Query itemsQuery = getQuery(database);
        adapter = new ItemsFirebaseRecyclerAdapter(Item.class, R.layout.item_item, ItemViewHolder.class, itemsQuery);
        recycler.setAdapter(adapter);
    }

    // Method that gets the User ID and stores it in a string
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

    private class ItemsFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Item, ItemViewHolder> {
        public ItemsFirebaseRecyclerAdapter(Class<Item> modelClass, int modelLayout, Class<ItemViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @Override
        protected void populateViewHolder(ItemViewHolder viewHolder, Item model, int position) {
            final DatabaseReference postRef = getRef(position);

            // Set click listener for the whole post view
            final String itemKey = postRef.getKey();
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Launch PostDetailActivity
                    Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                    intent.putExtra(ItemDetailActivity.EXTRA_ITEM_KEY, itemKey);
                    startActivity(intent);
                }
            });

            viewHolder.bindToItem(model);
        }
    }
}
