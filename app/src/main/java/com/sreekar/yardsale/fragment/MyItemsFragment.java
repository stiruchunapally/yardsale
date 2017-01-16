package com.sreekar.yardsale.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/*
This fragment is shown when a user clicks on the My Items tab in the main page. This fragment shows
the items that the user that is logged in has listed.
 */

public class MyItemsFragment extends ItemsFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        String UID = getUid();

        Query myItemsQuery = databaseReference.child("user-items").child(UID).limitToFirst(100);
        return myItemsQuery;
    }
}
