package com.aquarloan.aquarloan;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class PhoneNumberAuthenticationActivity extends AppCompatActivity implements View.OnClickListener{

    // UI references.
    public Button btnVerify, btnSend;
    public String countryCode = "", mobileNumber;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public PhoneAuthProvider.ForceResendingToken token;
    public FirebaseAuth firebaseAuth;
    public ImageView imgVerifyDone, imgSendDone, imgMobile;
    public Toolbar toolbar;
    public AppBarLayout appBarLayout;
    public TextView toolbarTitle;

    private ScrollView mSignUpView;
    public EditText mMobileNumberView, mVerificationCodeView;
    private TextView tvPromptSent, tvTimer, tvCountryCode;
    private View mProgressView;
    private ProgressBar sendProgress, verifyProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String verifiedId, verificationCode;

    private Integer smsValidityTime = 120;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_authentication);

        //TOOLBAR SETUP
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);

        toolbarTitle.setText(getTitle().toString());
        toolbar.getBackground().setAlpha(0);

        //TOOLBAR BACKBUTTON
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //VIEW INITIALIZATION
        tvPromptSent = (TextView) findViewById(R.id.tvPromptSent);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        mSignUpView = (ScrollView) findViewById(R.id.signup_form);
        tvCountryCode = (TextView) findViewById(R.id.countryCode);
        mMobileNumberView = (EditText) findViewById(R.id.edMobileNumberSignUp);
        mVerificationCodeView = (EditText) findViewById(R.id.edVerificationCode);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnSend = (Button) findViewById(R.id.btnSend);

        imgMobile = (ImageView) findViewById(R.id.imgMobile);
        imgSendDone = (ImageView) findViewById(R.id.imgSendDone);
        imgVerifyDone = (ImageView) findViewById(R.id.imgVerifyDone);

        sendProgress = (ProgressBar) findViewById(R.id.sendProgress);
        verifyProgress = (ProgressBar) findViewById(R.id.verifyProgress);

        //FIREBASE AUTHENTICATION INITIALIZATION
        firebaseAuth = FirebaseAuth.getInstance();

        //SET ONCLICK LISTENERS
        btnSend.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        //VIEWS VISIBILITY SETUP
        imgSendDone.setVisibility(View.GONE);
        imgVerifyDone.setVisibility(View.GONE);
        sendProgress.setVisibility(View.GONE);
        verifyProgress.setVisibility(View.GONE);
        mVerificationCodeView.setVisibility(View.GONE);
        btnVerify.setVisibility(View.GONE);

        tvPromptSent.setVisibility(View.GONE);
        tvTimer.setVisibility(View.GONE);

        //SET COUNTRY CODE
        setCountryCode();

        //CALLBACK AND TIMER FUNCTION FOR SMS VERIFICATION
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyProgress.setVisibility(View.GONE);
                imgVerifyDone.setVisibility(View.VISIBLE);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mMobileNumberView.setEnabled(true);
                sendProgress.setVisibility(View.GONE);
                imgMobile.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
                Toast.makeText(PhoneNumberAuthenticationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                btnSend.setVisibility(View.GONE);
                btnVerify.setVisibility(View.VISIBLE);
                mVerificationCodeView.setVisibility(View.VISIBLE);
                tvPromptSent.setVisibility(View.VISIBLE);
                tvPromptSent.setText(R.string.prompt_sms_sent);
                tvTimer.setVisibility(View.VISIBLE);
                sendProgress.setVisibility(View.GONE);
                imgSendDone.setVisibility(View.VISIBLE);
                verifiedId = verificationId;

                //Timer
                new CountDownTimer(smsValidityTime*1000, 1000){

                    @Override
                    public void onTick(long millisUntilFinished) {
                        tvTimer.setText(""+new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
                    }

                    @Override
                    public void onFinish() {
                        //Enable textView and button
                        mMobileNumberView.setEnabled(true);
                        btnSend.setEnabled(true);

                        //Hide all verification elements
                        imgSendDone.setVisibility(View.GONE);
                        sendProgress.setVisibility(View.GONE);
                        verifyProgress.setVisibility(View.GONE);
                        mVerificationCodeView.setVisibility(View.GONE);
                        btnVerify.setVisibility(View.GONE);
                        tvTimer.setVisibility(View.GONE);
                        tvPromptSent.setVisibility(View.GONE);

                        //Show mobile image and send button
                        btnSend.setVisibility(View.VISIBLE);
                        imgMobile.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        };

    }

    //RESERVED FUNCTION FOR SETTING UP THE COUNTRY CODE USING SPINNER
    private void setCountryCode() {
        countryCode = "63";
        tvCountryCode.setText(countryCode);
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
            mobileNumber = mMobileNumberView.getText().toString();

            if (TextUtils.isEmpty(mobileNumber)) {
                Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
            }

            else {
                imgMobile.setVisibility(View.GONE);
                sendProgress.setVisibility(View.VISIBLE);
                mMobileNumberView.setEnabled(false);
                btnSend.setEnabled(false);
                mobileNumber = countryCode + mobileNumber;

                if (firebaseAuth.getCurrentUser() != null){

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mobileNumber,        // Phone number to verify
                            smsValidityTime,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            mCallbacks,         // OnVerificationStateChangedCallbacks
                            token);             // Force Resend SMS
                }

                else {
                    resendVerificationCode(mobileNumber, token);
                }
            }


        }

        if (v == btnVerify) {
            verificationCode = mVerificationCodeView.getText().toString();
            if (TextUtils.isEmpty(verificationCode)){
                Toast.makeText(this, "Verification code is empty", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyProgress.setVisibility(View.VISIBLE);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifiedId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            }
        }
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                smsValidityTime,                 // Timeout duration
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
                            verifyProgress.setVisibility(View.GONE);
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
                                verifyProgress.setVisibility(View.GONE);
                                Toast.makeText(PhoneNumberAuthenticationActivity.this, "Verification code is invalid.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}

