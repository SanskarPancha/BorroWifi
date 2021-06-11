package com.hitket.borrowifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Owner_status extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "Owner Status";
    RadioButton rd_known, rd_moving, rd_unknown, rd_fixed;

    SwitchCompat switch_location, switch_turn_on_location;
    private FusedLocationProviderClient fusedLocationClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static String user_uid,map_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_status);
        rd_known = findViewById(R.id.radio_known);
        rd_unknown = findViewById(R.id.radio_unknown);
        rd_fixed = findViewById(R.id.radio_fixed);
        rd_moving = findViewById(R.id.radio_moving);
        switch_location = findViewById(R.id.switch_location2);
        switch_turn_on_location = findViewById(R.id.switch_gps2);

        user_uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        switch_location.setOnCheckedChangeListener(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        switch_turn_on_location.setOnCheckedChangeListener(this);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();


        // Check which radio button was clicked
        switch (view.getId()) {

            case R.id.radio_fixed:
                if (checked)

                break;
            case R.id.radio_moving:
                if (checked)

                break;
            case R.id.radio_unknown:
                if (checked)

                    break;
            case R.id.radio_known:
                if (checked)

                    break;

        }
    }

    public void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void makeLocationVisible(View view) {

        final TextView tv = findViewById(R.id.tv_location);
        RadioGroup rg = findViewById(R.id.radio_group_location);

        rg.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);

    }

    public void makeLocationInvisible(View view) {

        final TextView tv = findViewById(R.id.tv_location);
        RadioGroup rg = findViewById(R.id.radio_group_location);

        rg.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);

    }


    public void setStatus(View view) {
        if (!switch_location.isChecked()) {
            makeToast("Location is needed for client");
            switch_location.setBackgroundResource(R.color.light_red);
        } else if (!switch_turn_on_location.isChecked()) {
            makeToast("GPS must be turn ON");
            switch_turn_on_location.setBackgroundResource(R.color.light_red);
        } else if (rd_known.isChecked()) {
            switch_turn_on_location.setBackgroundResource(android.R.color.transparent);
            switch_location.setBackgroundResource(android.R.color.transparent);
            startActivity(new Intent(Owner_status.this, share_wifi.class));
        } else if (rd_unknown.isChecked() && rd_fixed.isChecked()) {
            //set Available on demand on map
            send_location_to_cloud();
            //show a dialog
        } else if (rd_unknown.isChecked() && rd_moving.isChecked()) {
            //set Available on demand on map
            //start service for location tracking
            makeToast("Moving");
        } else {
            makeToast("Invalid Hotspot Properties");
        }
    }

    public void clientStatusInfo(View view) {
        DialogFragment dialog = new popup_dialog();
        Bundle bun = new Bundle();
        bun.putString("key", getString(R.string.client_status_info));
        dialog.setArguments(bun);
        dialog.show(getSupportFragmentManager(), "Client info Dialog");
    }

    public void locationInfo(View view) {
        DialogFragment dialog = new popup_layout();
        Bundle bun = new Bundle();
        bun.putString("key", getString(R.string.location_status_info));
        dialog.setArguments(bun);
        dialog.show(getSupportFragmentManager(), "location info Dialog");
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
                makeToast("map");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.switch_location2:
                if (switch_location.isChecked()) {
                    check_permission();
                } else {

                }
                break;
            case R.id.switch_gps2:
                if (switch_turn_on_location.isChecked()) {
                    turn_on_location();
                } else {

                }

        }


    }

    private void turn_on_location() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                    switch_turn_on_location.setBackgroundResource(android.R.color.transparent);

                    switch_turn_on_location.setChecked(true);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            switch_turn_on_location.setChecked(false);
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        Owner_status.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            if (switch_turn_on_location.isChecked())
                                switch_turn_on_location.setChecked(false);
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });


    }


    public void check_permission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            makeToast("Location Access is granted");
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 420);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 420) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeToast("Location Permission Granted");
                switch_location.setBackgroundResource(android.R.color.transparent);

            } else {
                switch_location.setChecked(false);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.i(TAG, "onActivityResult: GPS Enabled by user");
                        switch_turn_on_location.setBackgroundResource(android.R.color.transparent);
                        switch_turn_on_location.setChecked(true);
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.i(TAG, "onActivityResult: User rejected GPS request");
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    public void send_location_to_cloud() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            makeToast("Error in getting GPS Location");
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        DocumentReference docRef = db.collection("users").document(user_uid)
                                .collection("hotspot_status").document("hotspot");
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        map_id = document.getString("map id");
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                        if (location != null) {

                            Map<String, Object> data_temp = new HashMap<>();
                            data_temp.put("lat", location.getLatitude());
                            data_temp.put("log",location.getLongitude());

                            db.collection("map").document("status").collection("aod")
                                    .add(data_temp).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "You will be notified when a client comes nearby");
                                    map_id = documentReference.getId();
                                    makeToast(map_id);
                                    makeToast("You will be notified when a client comes nearby ");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    makeToast("Error");
                                    Log.w(TAG, "Error in getting location " + e);
                                }
                            });
                            Map<String, Object> temp = new HashMap<>();
                            temp.put("map id", map_id);
                            db.collection("users").document(user_uid)
                                    .collection("hotspot_status").document("hotspot")
                                    .set(temp,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"map information succesfully sent to Firestore");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG,"Error in writing to cloud (map ID)" + e);
                                }
                            });
                        }
                    }
                });
    }
}