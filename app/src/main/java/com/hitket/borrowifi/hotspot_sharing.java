package com.hitket.borrowifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class hotspot_sharing extends AppCompatActivity {

    private static final String TAG = "hotspot_sharing";
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude;
    private double longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotspot_sharing);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {

                        send_location_to_cloud(location);
                    }
                });



    }


    private void send_location_to_cloud(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String user_uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        Map<String, Object> data_location = new HashMap<>();
        data_location.put("latitude",latitude);
        data_location.put("longitude",longitude);
        data_location.put("uid",user_uid);

        db.collection("map").document(user_uid)
                .set(data_location)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User location successfully sent to Firestore"))
                .addOnFailureListener(e -> Log.w(TAG, "Error in writing location to cloud " + e));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.map_ownerstatus:
                startActivity(new Intent(hotspot_sharing.this,MapsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    public void goto_home(View view) {
        startActivity(new Intent(hotspot_sharing.this,MainActivity.class));

    }
}

