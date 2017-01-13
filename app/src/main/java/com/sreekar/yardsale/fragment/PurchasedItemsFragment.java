package com.sreekar.yardsale.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/*
This fragment is shown when a user clicks on the purchased items tab. This fragment shows the
items a user has purchased.
 */
public class PurchasedItemsFragment extends ItemsFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query recentPostsQuery = databaseReference.child("items")
                .limitToFirst(100);
        return recentPostsQuery;
    }
}
