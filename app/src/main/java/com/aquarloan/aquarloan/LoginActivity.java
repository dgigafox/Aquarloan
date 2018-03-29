package com.aquarloan.aquarloan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.aquarloan.aquarloan.Interfaces.UserLoginCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {


    // UI references.
    private EditText mMobileNumberView;
    private EditText mPasswordView;
    private Button btnLoginDialogBox, btnLogin, btnCancel, btnRegister;
    private View mProgressView;
    private View mLoginFormView;
    private ScrollView mLoginView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private String encryptedPassword;
    private DatabaseReference databaseReference, users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.


        /*mLoginView = (ScrollView) findViewById(R.id.login_form);*/

        btnLoginDialogBox = (Button) findViewById(R.id.btnLoginDialogBox);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        /*progressBar = (ProgressBar) findViewById(R.id.login_progress);*/

        firebaseAuth = firebaseAuth.getInstance();
        btnLoginDialogBox.setOnClickListener(this);
        btnRegister.setOnClickListener(this);



        if(firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onClick(View view) {
        if(view == btnLoginDialogBox) {
            //CUSTOM DIALOG BOX FOR LOGIN
            CustomDialogLogin customDialogLogin = new CustomDialogLogin(LoginActivity.this);
            customDialogLogin.show();

        }
        if(view == btnRegister) {
            Intent intent = new Intent(this, PhoneNumberAuthenticationActivity.class);
            startActivity(intent);
        }
    }




}

