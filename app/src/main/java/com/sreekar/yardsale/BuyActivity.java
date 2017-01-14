package com.sreekar.yardsale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
import com.sreekar.yardsale.viewholder.ItemViewHolder;

import java.util.HashMap;
import java.util.Map;

import static com.sreekar.yardsale.ItemDetailActivity.EXTRA_ITEM_KEY;

public class BuyActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BuyActivity";

    private DatabaseReference mDatabase;
    private DatabaseReference mItemReference;

    private ValueEventListener mItemListener;

    private EditText etCreditCard;
    private EditText etAddress;
    private Button btnSubmit;

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

        etAddress = (EditText) findViewById(R.id.etAddress);
        etCreditCard = (EditText) findViewById(R.id.etCreditCard);
        btnSubmit = (Button) findViewById(R.id.button_submit);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Database
        mItemReference = FirebaseDatabase.getInstance().getReference()
                .child("items").child(itemKey);

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                item = dataSnapshot.getValue(Item.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadItem:onCancelled", databaseError.toException());
                Toast.makeText(BuyActivity.this, "Failed to load item.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        mItemReference.addValueEventListener(itemListener);

        // Keep copy of post listener so we can remove it when app stops
        mItemListener = itemListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mItemListener != null) {
            mItemReference.removeEventListener(mItemListener);
        }
    }

    private void submit() {
        String userId = getUid();

        if (!validateForm()) {
            return;
        }

        Map<String, Object> childUpdates = new HashMap<>();
        item.setPurchased(true);
        childUpdates.put("/items/" + itemKey, item);
        childUpdates.put("/purchased-items/" + userId + "/" + itemKey, item);

        mDatabase.updateChildren(childUpdates);

        Toast.makeText(BuyActivity.this, "Purchase auccessfull", Toast.LENGTH_SHORT).show();

        // Go to MainActivity
        startActivity(new Intent(BuyActivity.this, MainActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(etCreditCard.getText().toString())) {
            etCreditCard.setError("Required");
            result = false;
        } else {
            etCreditCard.setError(null);
        }

        if (TextUtils.isEmpty(etAddress.getText().toString())) {
            etAddress.setError("Required");
            result = false;
        } else {
            etAddress.setError(null);
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        submit();
    }
}
