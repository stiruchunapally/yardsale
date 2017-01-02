package com.sreekar.yardsale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;

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
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) itemImage.getDrawable());
        Bitmap bitmap = bitmapDrawable.getBitmap();
        String userId = getUid();

        Item item = new Item();
        item.setCondition(ratingCondition.getRating());
        item.setDescription(description.getText().toString());
        item.setImage(encodeBitmap(bitmap));
        item.setPrice(Float.valueOf(suggestedPrice.getText().toString()));
        item.setSellerName(getUserName());
        item.setTitle(title.getText().toString());

        String key = mDatabase.child("items").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/items/" + key, item);
        childUpdates.put("/user-items/" + userId + "/" + key, item);

        mDatabase.updateChildren(childUpdates);

        // Go to MainActivity
        startActivity(new Intent(DonateActivity.this, MainActivity.class));
        finish();
    }

    private String encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }
}
