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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView tvHello;
    private Button saveBtn, signOutBtn;
    private EditText edFirstName, edLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = firebaseAuth.getInstance();
        tvHello = (TextView) findViewById(R.id.tvHello);
        edFirstName = (EditText) findViewById(R.id.edFirstName);
        edLastName = (EditText) findViewById(R.id.edLastName);
        signOutBtn = (Button) findViewById(R.id.signOutBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);


        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();


        FirebaseUser user = firebaseAuth.getCurrentUser();

        tvHello.setText("Hello " + user.getPhoneNumber());

        signOutBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

    }

    private void saveUserInformation(){
        String firstName = edFirstName.getText().toString().trim();
        String lastName = edLastName.getText().toString().trim();

        UserInformation userInformation = new UserInformation(firstName, lastName);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userInformation);
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

