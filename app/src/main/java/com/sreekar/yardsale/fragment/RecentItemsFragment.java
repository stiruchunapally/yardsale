package com.sreekar.yardsale.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/*
This fragment is shown when a user clicks on the Recent Items tab or when they log in and open
the app. This fragment shows the last 100 items listed by various users.
 */

public class RecentItemsFragment extends ItemsFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        Query recentItemsQuery = databaseReference.child("items").limitToFirst(100);
        return recentItemsQuery;
    }
}
