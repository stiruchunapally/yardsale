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

    private DatabaseReference itemsReference;

    private TextView money;
    private Float moneyRaised = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize Database
        itemsReference = FirebaseDatabase.getInstance().getReference().child("items");

        // Initialize views
        money = (TextView) findViewById(R.id.tvMoney);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        itemsReference.addListenerForSingleValueEvent(new AboutValueEventListener());
    }

    /**
     * ValueEventListener for loading items from database
     */
    private class AboutValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot itemDataSnapshot : dataSnapshot.getChildren()) {
                Item item = itemDataSnapshot.getValue(Item.class);
                if (item.isPurchased() == true) {
                    //Formatting price with $ before the number and 2 places after the decimal
                    moneyRaised = moneyRaised + item.getPrice();
                }

                money.setText(String.format("$%.2f", moneyRaised));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
            Toast.makeText(AboutActivity.this, "Failed to load item.", Toast.LENGTH_SHORT).show();
        }
    }
}
