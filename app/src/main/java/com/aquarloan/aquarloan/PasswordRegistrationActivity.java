package com.aquarloan.aquarloan;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aquarloan.aquarloan.Interfaces.UserLoginCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EditText edSignUpPassword, edSignUpConfirmPassword;
    private Button btnSubmit;
    private FirebaseUser user;
    private String encryptedPassword, mobileNumber;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AppBarLayout appBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_registration);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);

        toolbarTitle.setText(getTitle().toString());
        toolbar.getBackground().setAlpha(0);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        edSignUpPassword = (EditText) findViewById(R.id.edSignUpPassword);
        edSignUpConfirmPassword = (EditText) findViewById(R.id.edSignUpConfirmPassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        user = firebaseAuth.getCurrentUser();

        mobileNumber = getIntent().getStringExtra("mobileNumber");

        btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            String password = edSignUpPassword.getText().toString();
            String confirmPassword = edSignUpConfirmPassword.getText().toString();

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            encryptedPassword = convertPassMd5(password);
            saveUserPassword();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                signOut(firebaseAuth, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        signOut(firebaseAuth, this);
        super.onBackPressed();
    }

    //NEED FUNCTION TO DISPLAY SET PASSWORD SCREEN ONSTOP OF THIS ACTIVITY
    /*@Override
    public void onStop(){
        firebaseAuth.signOut();
        super.onStop();
    }*/

    public void signOut(FirebaseAuth firebaseAuth, Activity activity) {
        firebaseAuth.signOut();
        finish();
        Intent intent = new Intent(activity, LoginActivity.class);
        startActivity(intent);
    }

    private void saveUserPassword() {

        UserLoginCredentials userLoginCredentials = new UserLoginCredentials(encryptedPassword, mobileNumber);
        databaseReference.child(user.getUid()).setValue(userLoginCredentials);
        Toast.makeText(this, "You are now registered", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PasswordRegistrationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public static String convertPassMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }
}
