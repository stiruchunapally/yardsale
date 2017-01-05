package com.sreekar.yardsale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sreekar.yardsale.models.Item;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DonateActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;

    private ImageButton btnCaptureImage;
    private Button btnSubmit;
    private EditText title;
    private ImageView itemImage;
    private RatingBar ratingCondition;
    private EditText suggestedPrice;
    private EditText description;

    private final int REQUEST_IMAGE_CAPTURE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Views
        btnCaptureImage = (ImageButton) findViewById(R.id.button_capture_image);
        btnSubmit = (Button) findViewById(R.id.button_submit);
        itemImage = (ImageView) findViewById(R.id.item_image);
        title = (EditText) findViewById(R.id.text_item_title);
        ratingCondition = (RatingBar) findViewById(R.id.rating_condition);
        suggestedPrice = (EditText) findViewById(R.id.text_price);
        description = (EditText) findViewById(R.id.text_description);

        // Click listeners
        btnSubmit.setOnClickListener(this);
        btnCaptureImage.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            itemImage.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_capture_image) {
            captureImage();
        } else if (i == R.id.button_submit) {
            submit();
        }
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void submit() {
        String userId = getUid();

        if (!validateForm()) {
            return;
        }

        Item item = new Item();
        item.setCondition(ratingCondition.getRating());
        item.setDescription(description.getText().toString());
        item.setPrice(Float.valueOf(suggestedPrice.getText().toString()));
        item.setSellerName(getUserName());
        item.setTitle(title.getText().toString());

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) itemImage.getDrawable());
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            item.setImage(encodeBitmap(bitmap));
        }

        String key = mDatabase.child("items").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/items/" + key, item);
        childUpdates.put("/user-items/" + userId + "/" + key, item);

        mDatabase.updateChildren(childUpdates);

        // Go to MainActivity
        startActivity(new Intent(DonateActivity.this, MainActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(suggestedPrice.getText().toString())) {
            suggestedPrice.setError("Required");
            result = false;
        } else {
            suggestedPrice.setError(null);
        }

        if (TextUtils.isEmpty(description.getText().toString())) {
            description.setError("Required");
            result = false;
        } else {
            description.setError(null);
        }

        if (TextUtils.isEmpty(title.getText().toString())) {
            title.setError("Required");
            result = false;
        } else {
            title.setError(null);
        }

        if (itemImage.getDrawable() == null) {
            Toast.makeText(DonateActivity.this, "Image required", Toast.LENGTH_SHORT).show();
            result = false;
        }

        if (ratingCondition.getRating() == 0) {
            Toast.makeText(DonateActivity.this, "Rating required", Toast.LENGTH_SHORT).show();
            result = false;
        }

        return result;
    }

    private String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
