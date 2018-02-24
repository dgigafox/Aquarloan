package com.aquarloan.aquarloan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {


    // UI references.
    private EditText mMobileNumberView;
    private EditText mPasswordView;
    private Button loginBtn;
    private Button signUpNowBtn;
    private View mProgressView;
    private View mLoginFormView;
    private ScrollView mLoginView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        mMobileNumberView = (EditText) findViewById(R.id.mobileNumber);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginView = (ScrollView) findViewById(R.id.login_form);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        signUpNowBtn = (Button) findViewById(R.id.signUpNowBtn);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        firebaseAuth = firebaseAuth.getInstance();
        loginBtn.setOnClickListener(this);
        signUpNowBtn.setOnClickListener(this);

        if(firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v) {
        if(v == loginBtn) {
            loginUser();
        }
        if(v == signUpNowBtn) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    protected void loginUser(){
        String mobileNumber = mMobileNumberView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if (TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mLoginView.setVisibility(View.GONE);

        firebaseAuth.signInWithEmailAndPassword(mobileNumber, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            mLoginView.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }
}

