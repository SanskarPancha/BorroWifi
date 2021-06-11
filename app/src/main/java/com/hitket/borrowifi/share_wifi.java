package com.hitket.borrowifi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class share_wifi extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "share_wifi";


    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText data;
    TextView ssid,password;
    private static final String ALLOWED_CHARACTERS ="asdfghjklpoiuymnbtvrcexwzq";
    private static final String ALLOWED_NUMBERS ="as0df1gh2jk3l45poi6u7ym8nbtvrce9xwzq";

    private static String hotspot_random_name="",hotspot_random_password="",user_uid;
    FirebaseUser F_user=FirebaseAuth.getInstance().getCurrentUser();
    SwitchCompat switch_location_access,switch_turn_on_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_wifi);

        ssid= findViewById(R.id.tv_name_compat);
        password = findViewById(R.id.tv_password_compat);
        data = findViewById(R.id.et_data_shared);
        user_uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        switch_location_access = findViewById(R.id.switch_location);
        switch_location_access.setOnCheckedChangeListener(this);

        switch_turn_on_location = findViewById(R.id.switch_gps);
        switch_turn_on_location.setOnCheckedChangeListener(this);


        if(F_user.getMetadata().getCreationTimestamp()==F_user.getMetadata().getLastSignInTimestamp()) {
            random_generator();
            set_new_user();
        }
        else{
            db.collection("users").document(user_uid).collection("hotspot_status")
                    .document("hotspot").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                hotspot_random_name=  document.get("ssid").toString();
                                hotspot_random_password= document.get("password").toString();
                                ssid.setText(hotspot_random_name);
                                password.setText(hotspot_random_password);
                            }
                            else{

                            }
                        } else {
                            random_generator();
                            set_new_user();
                            Log.w(TAG, "Error getting documents for ssid and password", task.getException());
                        }
                    });

        }

    }

    private void set_new_user() {

        Map<String, Object> user = new HashMap<>();
        user.put("ssid",hotspot_random_name);
        user.put("password", hotspot_random_password);
        db.collection("users").document(user_uid).collection("hotspot_status")
                .document("hotspot")
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"User information succesfully sent to Firestore");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error in writing to cloud (new user)" + e);
            }
        });

    }

    private void random_generator() {

        hotspot_random_name = getRandomString(4,ALLOWED_CHARACTERS);
        hotspot_random_password = getRandomString(8,ALLOWED_NUMBERS);
        ssid.setText(hotspot_random_name);
        password.setText(hotspot_random_password);

    }

    private static String getRandomString(final int sizeOfRandomString,final String characters)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(characters.charAt(random.nextInt(characters.length())));
        return sb.toString();
    }

    public void hotspot_settings(View view) {
        Intent tetherSettings = new Intent();
        tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
        startActivity(tetherSettings);
    }

    public void start_sharing_hotspot(View view) {

        if(data.getText().toString().equals(""))
        {data.setError("Field Required");}
        else if(!switch_location_access.isChecked()){
            makeToast("Location is needed for client");
            switch_location_access.setBackgroundResource(R.color.light_red);
        }
        else if(!switch_turn_on_location.isChecked()){
            makeToast("GPS must be turn ON");
            switch_turn_on_location.setBackgroundResource(R.color.light_red);
        }
        else {
            switch_turn_on_location.setBackgroundResource(android.R.color.transparent);
            switch_location_access.setBackgroundResource(android.R.color.transparent);
            data.setError(null);
            int data_int = Integer.parseInt(data.getText().toString());


            Map<String, Object> data_temp = new HashMap<>();
            data_temp.put("status","");
            data_temp.put("data limit",data_int);
            data_temp.put("data shared",0);
            data_temp.put("ssid",hotspot_random_name);
            data_temp.put("password", hotspot_random_password);


            db.collection("users").document(user_uid).collection("hotspot_status").document("hotspot")
                    .set(data_temp).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "User information succesfully sent to Firestore (hotspot ON)");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error in writing to cloud( Hotspot ON ) " + e);
                }
            });

            startActivity(new Intent(share_wifi.this, hotspot_sharing.class));

        }
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

    public void makeToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void refresh_password(View view) {
        String pass = getRandomString(8,ALLOWED_NUMBERS);
        password.setText(pass);
        Map<String, Object> map = new HashMap<>();
        map.put("password", pass);
        db.collection("users").document(user_uid).collection("hotspot_status").document("hotspot").set(map,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Password refresh succesfull");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Password refresh failed" + e );
            }
        });

    }

    public void refresh_ssid(View view) {

        String name =getRandomString(4,ALLOWED_CHARACTERS);

        ssid.setText(name);
        Map<String, Object> map = new HashMap<>();
        map.put("ssid", name);
        db.collection("users").document(user_uid).collection("hotspot_status").document("hotspot").set(map,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"hotspot name refresh successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"hotspot name refresh failed" + e );
            }
        });

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.switch_location:
                if(switch_location_access.isChecked()) {
                check_permission();
                }
                else{

                }
            break;
            case R.id.switch_gps:
                if(switch_turn_on_location.isChecked()) {
                    turn_on_location();
                }
                else{

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
                                        share_wifi.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            if(switch_turn_on_location.isChecked())
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
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            makeToast("Location Access is granted");
        }
        else{
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){

            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},420);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==420)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                makeToast("Location Permission Granted");
                switch_location_access.setBackgroundResource(android.R.color.transparent);

            }
            else{
                switch_location_access.setChecked(false);
            }
        }
        else{
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


}