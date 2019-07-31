package ior.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import utils.IorUtils;

public class HomeScreenActivity extends AppCompatActivity {

    private TextView textViewEmail;
    private Button buttonSignOut;
    private Button buttonDisconnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        buttonDisconnect = findViewById(R.id.buttonDisconnect);
        buttonSignOut = findViewById(R.id.buttonSignOut);
        textViewEmail = findViewById(R.id.textViewEmail);

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        textViewEmail.setText(email);


        buttonSignOut.setOnClickListener(view -> {

            IorUtils.signOut(this);

        });

        buttonDisconnect.setOnClickListener(view -> {

            IorUtils.revokeAccess(this);
        });

    }





}
