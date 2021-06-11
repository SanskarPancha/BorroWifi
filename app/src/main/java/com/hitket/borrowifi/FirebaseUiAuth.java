 package com.hitket.borrowifi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.firestore.FirebaseFirestore;

 public class FirebaseUiAuth extends AppCompatActivity {
    Button register;
    private static final int RC_SIGN_IN =6969;
     //private static final String TAG = "FirebaseUiAuth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui_auth);

        register = findViewById(R.id.bu_Login);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        register.setOnClickListener(v -> {

            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );

// Create and launch sign-in intent
            Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.single_borrowifi_icon)
                    .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                    .build();

            startActivityForResult(intent, RC_SIGN_IN);

        });


    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode==RC_SIGN_IN){
                if(resultCode==RESULT_OK){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.getMetadata().getCreationTimestamp()==user.getMetadata().getLastSignInTimestamp()) {


                        Toast.makeText(this, "Welcome to BorroWifi", Toast.LENGTH_SHORT).show();



                    }
                    else
                        Toast.makeText(this, "Welcome back to BorroWifi", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(this,MainActivity.class);
                    startActivity(intent);
                    this.finish();
                }
                else
                {

                    //sign in failed
                    IdpResponse response = IdpResponse.fromResultIntent(data);
                    if(response==null){
                        //user cancelled sign in
                    }
                    else{
                        // some other error
                    }
                }

            }
        }

    }
