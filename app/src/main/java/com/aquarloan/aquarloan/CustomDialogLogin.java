package com.aquarloan.aquarloan;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Darren Gegantino on 3/5/2018.
 */

public class CustomDialogLogin extends Dialog implements View.OnClickListener {


    private final Activity activity;
    private String mobileNumber, password, encryptedPassword;
    private Button btnLogin, btnCancel;
    private EditText etMobileNumber, etPassword;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    public CustomDialogLogin(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login);

        //VIEWS INITIALIZATION
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        etMobileNumber = (EditText) findViewById(R.id.mobileNumber);
        etPassword = (EditText) findViewById(R.id.password);

        progressDialog = new ProgressDialog(activity);

        //SET CURSOR TO LAST TEXT etMobileNumber
        etMobileNumber.setSelection(etMobileNumber.getText().length());

        //FIREBASE AUTHENTICATION INITIALIZATION
        firebaseAuth = firebaseAuth.getInstance();

        //BUTTONS ONCLICK LISTENERS
        btnLogin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == btnLogin) {

            mobileNumber = etMobileNumber.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(mobileNumber)) {
                Toast.makeText(activity, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(activity, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            new LoginTask(mobileNumber, password).execute();
        }

        if(v == btnCancel) {
            dismiss();
        }
    }

    //LOGIN ASYNC TASK
    private class LoginTask extends AsyncTask<Void, Void, Void> {
        String mobileNumber, password;

        public LoginTask(String mobileNumber, String password){
            this.mobileNumber = mobileNumber;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... params) {
            loginUser(mobileNumber, password);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final Void unused) {
            progressDialog.dismiss();
        }
    }

    public void loginUser(final String mobileNumber, final String password){
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
                    Toast.makeText(activity, "Successful login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, PhoneNumberReAuthenticationActivity.class);
                    intent.putExtra("mobileNumber",mobileNumber);
                    activity.startActivity(intent);
                }
                else {
                    Toast.makeText(activity, "Wrong username and password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
