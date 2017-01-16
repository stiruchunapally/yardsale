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

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sreekar.yardsale.models.Item;
import com.sreekar.yardsale.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.sreekar.yardsale.utils.ImageUtils.encodeBitmap;

/**
 * This activity is shown when a user clicks on the donate button in the main page.
 * This page allows for the input of an items title, condition, description, suggested price
 * and picture. When a user inputs all of the information and clicks the submit button,
 * the information is shown on the main page and the user is directed to the main page.
 * If all of the information is not submitted, an error message is shown.
 */
public class DonateActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference database;

    private ImageButton captureImageButton;
    private Button submitButton;
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

        // Get firebase database reference
        database = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        itemImage = (ImageView) findViewById(R.id.item_image);
        title = (EditText) findViewById(R.id.text_item_title);
        ratingCondition = (RatingBar) findViewById(R.id.rating_condition);
        suggestedPrice = (CurrencyEditText) findViewById(R.id.text_price);
        description = (EditText) findViewById(R.id.etAddress);
        captureImageButton = (ImageButton) findViewById(R.id.button_capture_image);
        submitButton = (Button) findViewById(R.id.button_submit);

        // Setup click listeners
        submitButton.setOnClickListener(this);
        captureImageButton.setOnClickListener(this);
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
        // make sure all the fields are set
        if (!validateForm()) {
            return;
        }

        updateDatabase();

        Toast.makeText(DonateActivity.this, "Submittion successfull", Toast.LENGTH_SHORT).show();

        // Go to MainActivity
        startActivity(new Intent(DonateActivity.this, MainActivity.class));
        finish();
    }

    private void updateDatabase() {
        Item item = new Item();
        item.setCondition(ratingCondition.getRating());
        item.setDescription(description.getText().toString());
        String strSuggestedPrice = suggestedPrice.getText().toString();
        if (strSuggestedPrice.startsWith("$")) {
            strSuggestedPrice = strSuggestedPrice.substring(1);
        }
        item.setPrice(Float.valueOf(strSuggestedPrice));
        item.setSellerName(getUserName());
        item.setTitle(title.getText().toString());
        item.setPurchased(false);

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) itemImage.getDrawable());
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            item.setImage(encodeBitmap(bitmap));
        }

        String key = database.child("items").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/items/" + key, item);
        childUpdates.put("/user-items/" + getUid() + "/" + key, item);

        database.updateChildren(childUpdates);
    }

    /**
     * Validates that all the fields are set, returns false if any of the required fields is not set.
     */
    private boolean validateForm() {
        boolean result = true;

        suggestedPrice.setError(null);
        description.setError(null);
        title.setError(null);

        if (TextUtils.isEmpty(suggestedPrice.getText().toString())) {
            suggestedPrice.setError("Required");
            result = false;
        }

        if (TextUtils.isEmpty(description.getText().toString())) {
            description.setError("Required");
            result = false;
        }

        if (TextUtils.isEmpty(title.getText().toString())) {
            title.setError("Required");
            result = false;
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
}
