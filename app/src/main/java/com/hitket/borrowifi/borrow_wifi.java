package com.hitket.borrowifi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class borrow_wifi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_wifi);

        Intent intent = new Intent(borrow_wifi.this,MapsActivity.class);
        startActivity(intent);
    }
}