package com.aquarloan.aquarloan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberReauthenticationActivity extends PhoneNumberAuthenticationActivity implements View.OnClickListener {

    TextView tvPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_reauthentication);

        mobileNumber = getIntent().getStringExtra("mobileNumber");
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnSend = (Button) findViewById(R.id.btnSend);

        tvPhoneNumber.setText(mobileNumber);

        btnVerify.setOnClickListener(this);
        btnSend.setOnClickListener(this);

    }

    @Override
    protected void onStart(){
        super.onStart();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // Force Resend SMS
    }

    @Override
    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            imgVerifyDone.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(PhoneNumberReauthenticationActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("mobileNumber", mobileNumber);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneNumberReauthenticationActivity.this, "Verification code is invalid.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
