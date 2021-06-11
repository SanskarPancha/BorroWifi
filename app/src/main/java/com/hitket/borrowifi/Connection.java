package com.hitket.borrowifi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Connection extends AppCompatActivity {


    private static final String TAG = "Connection";
    private String uid_hotspot;
    TextView ssid,password;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        ssid= findViewById(R.id.tv_name_compat4);
        password = findViewById(R.id.tv_password_compat2);

        Intent myIntent = getIntent();

        uid_hotspot = myIntent.getStringExtra("uid");

        db.collection("users").document(uid_hotspot).collection("hotspot_status")
                .document("hotspot").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){

                            ssid.setText(document.get("ssid").toString());
                            password.setText(document.get("password").toString());
                        }
                        else{

                        }
                    } else {

                        Log.w(TAG, "Error getting documents for ssid and password", task.getException());
                    }
                });


    }
    public void copy_password_to_clipboard(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password copied",password.getText().toString());
        clipboard.setPrimaryClip(clip);
        makeToast("Hotspot Password Copied");
    }

    public void copy_ssid_to_clipboard(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("SSID copied", ssid.getText().toString());
        clipboard.setPrimaryClip(clip);

        makeToast("Hotspot name Copied");
    }
    public void hotspot_settings(View view) {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
       // startActivity(tetherSettings);
    }
    public void makeToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}