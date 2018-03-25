package com.aquarloan.aquarloan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aquarloan.aquarloan.Interfaces.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, databaseReferenceUsers, databaseReferenceCashPool;
    private TextView tvHello, tvCashPool;
    private Button saveBtn, signOutBtn;
    private EditText edFirstName, edLastName;
    private FirebaseUser user;
    private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = firebaseAuth.getInstance();
        tvCashPool = (TextView) findViewById(R.id.tvCashPool);
        tvHello = (TextView) findViewById(R.id.tvHello);
        edFirstName = (EditText) findViewById(R.id.edFirstName);
        edLastName = (EditText) findViewById(R.id.edLastName);
        signOutBtn = (Button) findViewById(R.id.signOutBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        mobileNumber = user.getPhoneNumber().replace("+","");

        if(user == null) {
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            databaseReference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild("password")){
                        Intent intent = new Intent(MainActivity.this,PasswordRegistrationActivity.class);
                        intent.putExtra("mobileNumber",mobileNumber);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        /*CustomDialogSetPassword customDialogSetPassword = new CustomDialogSetPassword(MainActivity.this, databaseReference, user, user.getPhoneNumber());
                        customDialogSetPassword.setCancelable(false);
                        customDialogSetPassword.show();*/
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        /*databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseReferenceCashPool = FirebaseDatabase.getInstance().getReference("cash_pool");*/
        displayCashPool();



        tvHello.setText("Hello " + user.getUid());

        signOutBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

    }

    private void displayCashPool() {
        // Attach a listener to read the data at our posts reference
        databaseReference.child("cash_pool").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cashPool = dataSnapshot.getValue().toString();
                tvCashPool.setText(cashPool);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    private void saveUserInformation(){
        String firstName = edFirstName.getText().toString().trim();
        String lastName = edLastName.getText().toString().trim();

        /*UserInformation userInformation = new UserInformation(firstName, lastName);*/
        //Testing HashMap below
        Map<String, Object> userInformation = new HashMap<>();
        userInformation.put("first_name",firstName);
        userInformation.put("last_name",lastName);

        databaseReference.child("users").child(user.getUid()).updateChildren(userInformation);
        Toast.makeText(this, "Information Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v == signOutBtn) {
            firebaseAuth.signOut();
            Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        if(v == saveBtn) {
            saveUserInformation();
        }
    }
}

