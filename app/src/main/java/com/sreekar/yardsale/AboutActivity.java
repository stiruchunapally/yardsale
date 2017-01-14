package com.sreekar.yardsale;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {

    private TextView Title;
    private TextView Money;
    private TextView MoneyRaisedText;
    private float MoneyRaised = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Title = (TextView) findViewById(R.id.tvAbout);
        Money = (TextView) findViewById(R.id.tvMoney);
        MoneyRaisedText = (TextView) findViewById(R.id.tvMoneyRaisedText);
    }
}
