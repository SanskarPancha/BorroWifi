package com.hitket.borrowifi;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class MainActivity extends AppCompatActivity {
    private AdView mAdView = findViewById(R.id.adView_mainmenu);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        if(FirebaseAuth.getInstance().getCurrentUser()== null)
        {
            Intent intent = new Intent(MainActivity.this,FirebaseUiAuth.class);
            startActivity(intent);
            finish();
        }
    }

    public void signout_handle() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent = new Intent(MainActivity.this, FirebaseUiAuth.class);
                            startActivity(intent);
                            finish();
                        }
                        else{

                            Toast toast= Toast.makeText(MainActivity.this,"Sign out Failed",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.my_profile:
                showMyProfile();
                return true;
            case R.id.logout_menu_id:
                signout_handle();
                return true;
            case R.id.map:
                open_map();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void open_map() {
        Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();

    }

    private void showMyProfile() {

        Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
    }

    public void share_wifi(View view) {

        startActivity(new Intent(MainActivity.this,Owner_status.class));
    }

    public void borrow_wifi(View view) {
        startActivity(new Intent(MainActivity.this,borrow_wifi.class));

    }
}

