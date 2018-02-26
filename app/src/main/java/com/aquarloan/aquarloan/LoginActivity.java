package com.aquarloan.aquarloan;

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
    private Button loginBtn;
    private Button signUpNowBtn;
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
            Intent intent = new Intent(this, PhoneNumberAuthenticationActivity.class);
            startActivity(intent);
        }
    }

    protected void loginUser(){

        final String mobileNumber = mMobileNumberView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if (TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        encryptedPassword = PasswordRegistrationActivity.convertPassMd5(password);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Map<String, Object> model = (Map<String, Object>) child.getValue();

                    if(model.get("phoneNumber").equals(mobileNumber) && model.get("password").equals(encryptedPassword)) {
                        exists = true;
                        break;
                    }
                }

                if(exists) {
                    Intent intent = new Intent(LoginActivity.this, PhoneNumberReauthenticationActivity.class);
                    intent.putExtra("mobileNumber",mobileNumber);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Wrong username and password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*firebaseAuth.signInWithEmailAndPassword(mobileNumber, password)
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
                });*/

    }
}

