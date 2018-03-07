package com.aquarloan.aquarloan;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import static com.aquarloan.aquarloan.ActionBarAlign.actionBarCenter;

/**
 * A login screen that offers login via email/password.
 */
public class PhoneNumberAuthenticationActivity extends AppCompatActivity implements View.OnClickListener{

    // UI references.
    public Button btnVerify, btnSend;
    public String mobileNumber;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public PhoneAuthProvider.ForceResendingToken token;
    public FirebaseAuth firebaseAuth;
    public ActionBar actionBar;

    private ScrollView mSignUpView;
    private EditText mMobileNumberView, mVerificationCodeView;
    private TextView tvPromptSent;
    private View mProgressView;
    private ImageView imgSendDone;
    public ImageView imgVerifyDone;
    private ProgressBar sendProgress, verifyProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String verifiedId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_authentication);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSignUpView = (ScrollView) findViewById(R.id.signup_form);
        tvPromptSent = (TextView) findViewById(R.id.tvPromptSent);
        mMobileNumberView = (EditText) findViewById(R.id.edMobileNumberSignUp);
        mVerificationCodeView = (EditText) findViewById(R.id.edVerificationCode);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnSend = (Button) findViewById(R.id.btnSend);
        imgSendDone = (ImageView) findViewById(R.id.imgSendDone);
        imgVerifyDone = (ImageView) findViewById(R.id.imgVerifyDone);

        sendProgress = (ProgressBar) findViewById(R.id.sendProgress);
        verifyProgress = (ProgressBar) findViewById(R.id.verifyProgress);

        firebaseAuth = FirebaseAuth.getInstance();
        btnSend.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        imgSendDone.setVisibility(View.GONE);
        imgVerifyDone.setVisibility(View.GONE);
        sendProgress.setVisibility(View.GONE);
        verifyProgress.setVisibility(View.GONE);
        mVerificationCodeView.setVisibility(View.GONE);
        btnVerify.setVisibility(View.GONE);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyProgress.setVisibility(View.GONE);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneNumberAuthenticationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mMobileNumberView.setEnabled(false);
                btnSend.setVisibility(View.GONE);
                btnVerify.setVisibility(View.VISIBLE);
                mVerificationCodeView.setVisibility(View.VISIBLE);
                tvPromptSent.setVisibility(View.VISIBLE);
                tvPromptSent.setText(R.string.prompt_sms_sent);
                sendProgress.setVisibility(View.GONE);
                verifiedId = verificationId;
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSend) {
            sendProgress.setVisibility(View.VISIBLE);
            mobileNumber = mMobileNumberView.getText().toString();

            if (firebaseAuth.getCurrentUser() != null){

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        mobileNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        mCallbacks,         // OnVerificationStateChangedCallbacks
                        token);             // Force Resend SMS
            }

            else {

                resendVerificationCode(mobileNumber, token);

            }




        }

        if (v == btnVerify) {
            verifyProgress.setVisibility(View.VISIBLE);
            String verificationCode = mVerificationCodeView.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifiedId, verificationCode);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            imgVerifyDone.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(PhoneNumberAuthenticationActivity.this, PasswordRegistrationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("mobileNumber", mobileNumber);
                            startActivity(intent);
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneNumberAuthenticationActivity.this, "Verification code is invalid.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}

