package com.hitket.borrowifi;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(FirebaseAuth.getInstance().getCurrentUser()== null)
        {
            Intent intent = new Intent(MainActivity.this,FirebaseUiAuth.class);
            startActivity(intent);
            finish();
        }

        allot_coins();

    }
    private void allot_coins() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String user_uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Map<String, Object> user1 = new HashMap<>();
        user1.put("balance",1000);
        db.collection("users").document(user_uid).collection("coins")
                .document("value")
                .set(user1).addOnSuccessListener(aVoid -> Log.d(TAG,"First time user's coin alloted successfully ")).addOnFailureListener(e -> Log.w(TAG,"Error in writing to cloud (First time user's coin allocation Failed )" + e));

    }

    public void signout_handle() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(task -> {
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
        //Toast.makeText(this, "Map", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this,MapsActivity.class));

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

