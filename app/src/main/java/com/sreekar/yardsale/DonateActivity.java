package com.sreekar.yardsale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class DonateActivity extends AppCompatActivity {

    private ImageButton buploadImage;
    private EditText etname;
    private EditText etprice;
    private EditText etdescription;
    private TextView tvcondition;
    private ImageView mImageLabel;

    private final int REQUEST_IMAGE_CAPTURE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        buploadImage = (ImageButton) findViewById(R.id.upload_image);
        mImageLabel = (ImageView) findViewById(R.id.itemImageView);

        buploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageLabel.setImageBitmap(imageBitmap);

//            BitmapDrawable bitmapDrawable = ((BitmapDrawable) mImageLabel.getDrawable());
//            Bitmap bitmap = bitmapDrawable .getBitmap();
        }
    }
}
