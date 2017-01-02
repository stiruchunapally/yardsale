package com.sreekar.yardsale.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class RecentItemsFragment extends ItemsFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query recentPostsQuery = databaseReference.child("items")
                .limitToFirst(100);
        return recentPostsQuery;
    }
}
