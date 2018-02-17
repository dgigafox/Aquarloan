package com.aquarloan.aquarloan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // UI references.
    private ScrollView mSignUpView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private Button registerBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignUpView = (ScrollView) findViewById(R.id.signup_form);
        mEmailView = (AutoCompleteTextView)findViewById(R.id.tvEmail);
        mPasswordView = (EditText) findViewById(R.id.tvPassword);
        mConfirmPasswordView = (EditText) findViewById(R.id.tvConfirmPassword);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        firebaseAuth = FirebaseAuth.getInstance();


        registerBtn.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.registerProgress);
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            registerUser();
        }
    }

    protected void registerUser() {
        final String email = mEmailView.getText().toString().trim();
        final String password = mPasswordView.getText().toString();
        final String confirmPassword = mConfirmPasswordView.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please enter your password confirmation", Toast.LENGTH_SHORT).show();
        }


        progressBar.setVisibility(View.VISIBLE);
        mSignUpView.setVisibility(View.GONE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "You are registered.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            finish();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(SignUpActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                            }
                            mSignUpView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

}

