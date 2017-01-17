package com.sreekar.yardsale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sreekar.yardsale.models.Item;

import java.util.HashMap;
import java.util.Map;

import static com.sreekar.yardsale.ItemDetailActivity.EXTRA_ITEM_KEY;

public class BuyActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BuyActivity";

    private DatabaseReference database;
    private DatabaseReference itemReference;

    private EditText creditCard;
    private EditText address;
    private Button submitButton;

    private String itemKey;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        // Get item key from intent
        itemKey = getIntent().getStringExtra(EXTRA_ITEM_KEY);
        if (itemKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_ITEM_KEY");
        }

        // Get firebase database reference
        database = FirebaseDatabase.getInstance().getReference();
        itemReference = FirebaseDatabase.getInstance().getReference().child("items").child(itemKey);

        // Initialize views
        address = (EditText) findViewById(R.id.etAddress);
        creditCard = (EditText) findViewById(R.id.etCreditCard);
        submitButton = (Button) findViewById(R.id.button_submit);

        // Setup click listeners
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        itemReference.addListenerForSingleValueEvent(new BuyValueEventListener());
    }

    @Override
    public void onClick(View v) {
        submit();
    }

    private void submit() {
        if (!validateForm()) {
            return;
        }

        updateDatabase();

        // Go to MainActivity
        startActivity(new Intent(BuyActivity.this, MainActivity.class));
        finish();
    }

    private void updateDatabase() {
        Map<String, Object> childUpdates = new HashMap<>();
        item.setPurchased(true);
        childUpdates.put("/items/" + itemKey, item);
        childUpdates.put("/purchased-items/" + getUid() + "/" + itemKey, item);
        childUpdates.put("/user-items/" + getUid() + "/" + itemKey, item);

        database.updateChildren(childUpdates);

        Toast.makeText(BuyActivity.this, "Purchase successfull", Toast.LENGTH_SHORT).show();
    }

    /**
     * Validates that all the fields are set, returns false if any of the required fields is not set.
     */
    private boolean validateForm() {
        boolean result = true;

        creditCard.setError(null);
        address.setError(null);

        if (TextUtils.isEmpty(creditCard.getText().toString())) {
            creditCard.setError("Required");
            result = false;
        }

        if (TextUtils.isEmpty(address.getText().toString())) {
            address.setError("Required");
            result = false;
        }

        return result;
    }

    /**
     * ValueEventListener for loading item details from database
     */
    private class BuyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            item = dataSnapshot.getValue(Item.class);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
            Toast.makeText(BuyActivity.this, "Failed to load item.", Toast.LENGTH_SHORT).show();
        }
    }
}
