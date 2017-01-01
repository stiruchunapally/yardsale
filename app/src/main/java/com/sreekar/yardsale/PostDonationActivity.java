package com.sreekar.yardsale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PostDonationActivity extends AppCompatActivity {

    private Button buploadImage;
    private EditText etname;
    private EditText etprice;
    private EditText etdescription;
    private TextView tvcondition;

    public void uploadimage(){
        buploadImage = (Button) findViewById(R.id.buploadImage);

        buploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                int pick = 0;
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, pick);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_donation);
        uploadimage();
    }
}
