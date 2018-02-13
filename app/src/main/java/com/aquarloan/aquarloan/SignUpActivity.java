package com.aquarloan.aquarloan;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button registerBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView)findViewById(R.id.tvEmail);
        mPasswordView = (EditText) findViewById(R.id.tvPassword);
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

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "You are registered.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                e.
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleAuthenticationException(e);
            }
        });
    }

    protected void handleAuthenticationException(@NonNull Exception e){
        if(e instanceof FirebaseAuthUserCollisionException){

        }
    }

/*    protected String catchError(String email, String password){
        try {
            throw firebaseAuth.createUserWithEmailAndPassword(email, password).getException();
        }

    }*/

}

