package com.sreekar.yardsale;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreekar.yardsale.models.Item;

public class AboutActivity extends BaseActivity {
    private static final String TAG = "BuyActivity";

    private DatabaseReference mDatabase;
    private DatabaseReference mItemsReference;

    private ValueEventListener mItemsListener;

    private TextView Money;
    private Float MoneyRaised = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Money = (TextView) findViewById(R.id.tvMoney);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Database
        mItemsReference = FirebaseDatabase.getInstance().getReference().child("items");
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemDataSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemDataSnapshot.getValue(Item.class);
                    if (item.isPurchased() == true) {
                        //Formatting price with $ before the number and 2 places after the decimal
                        MoneyRaised = MoneyRaised + item.getPrice();
                    }

                    Money.setText(String.format("$%.2f", MoneyRaised));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                Toast.makeText(AboutActivity.this, "Failed to load item.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mItemsReference.addValueEventListener(itemListener);

        // Keep copy of post listener so we can remove it when app stops
        mItemsListener = itemListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mItemsListener != null) {
            mItemsReference.removeEventListener(mItemsListener);
        }
    }
}
