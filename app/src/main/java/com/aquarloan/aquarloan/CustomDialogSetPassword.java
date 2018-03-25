package com.aquarloan.aquarloan;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aquarloan.aquarloan.Interfaces.UserLoginCredentials;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Darren Gegantino on 3/25/2018.
 */

public class CustomDialogSetPassword extends Dialog implements View.OnClickListener {

    private Activity activity;
    private EditText password, confirmPassword;
    private Button submit;
    private String encryptedPassword, mobileNumber;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    public CustomDialogSetPassword(Activity activity, DatabaseReference databaseReference, FirebaseUser user, String mobileNumber) {
        super(activity);
        this.activity = activity;
        this.databaseReference = databaseReference;
        this.user = user;
        this.mobileNumber = mobileNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_set_password);

        //VIEWS INITIALIZATION
        submit =(Button) findViewById(R.id.btnSubmit);
        password =(EditText) findViewById(R.id.edPassword);
        confirmPassword =(EditText) findViewById(R.id.edConfirmPassword);

        //ONCLICKLISTENTER
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == submit) {
            String passwordString = password.getText().toString();
            String confirmPasswordString = confirmPassword.getText().toString();

            if (TextUtils.isEmpty(passwordString)) {
                Toast.makeText(activity, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(confirmPasswordString)) {
                Toast.makeText(activity, "Please enter a password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordString.equals(confirmPasswordString)) {
                Toast.makeText(activity, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            encryptedPassword = PasswordRegistrationActivity.convertPassMd5(passwordString);
            saveUserPassword();
        }
    }

    public void saveUserPassword(){
        UserLoginCredentials userLoginCredentials = new UserLoginCredentials(encryptedPassword, mobileNumber);
        databaseReference.child("users").child(user.getUid()).setValue(userLoginCredentials);
        Toast.makeText(activity, "You are now registered", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
