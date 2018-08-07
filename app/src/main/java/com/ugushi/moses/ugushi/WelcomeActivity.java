package com.ugushi.moses.ugushi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.provider.GoogleProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

//http://melardev.com/eng/blog/2017/12/27/android-firebase-ui-authentication/
public class WelcomeActivity extends Activity {
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private Button btnSignIn;
    private TextView txtEmail;
    private TextView txtUser;
    private ImageView imgProfile;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        for (String provider : AuthUI.SUPPORTED_PROVIDERS) {
            Log.v(this.getClass().getName(), provider);
        }

        mAuth = FirebaseAuth.getInstance();
        imgProfile = (ImageView) findViewById(R.id.imageView);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtUser = (TextView) findViewById(R.id.txtUser);

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUi();
            }
        };

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                finish();
            }
        }, 2000);
        */
    }

    private void updateUi() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            txtEmail.setVisibility(View.GONE);
            txtUser.setVisibility(View.GONE);
            imgProfile.setImageBitmap(null);
        } else {
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            txtUser.setVisibility(View.VISIBLE);

            txtUser.setText(user.getDisplayName());
            txtEmail.setText(user.getEmail());
            //Picasso.with(ActivityFUIAuth.this).load(user.getPhotoUrl()).into(imgProfile);
        }
    }


    public void signOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(WelcomeActivity.this,"signed out succesfully ... ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                Log.d(this.getClass().getName(), "This user signed in with " + response.getProviderType());
                updateUi();
            } else {
                updateUi();
            }
        }
    }

    public void deleteAccount(View view) {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        //.setTheme(R.style.AppTheme)
                        .setTosUrl("https://superapp.example.com/terms-of-service.html")
                        .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                        .setLogo(R.drawable.ulogo)
                        .build(),
                RC_SIGN_IN);
    }

}
