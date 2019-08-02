package ior.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import ior.engine.AccountEngine;

public class MyAccountActivity extends AppCompatActivity {

    private TextView textViewEmail;
    private TextView textViewRegisterDate;
    private Button buttonRemoveAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        textViewEmail = findViewById(R.id.textView_myAccount_emailVal);
        textViewRegisterDate = findViewById(R.id.textView_myAccount_registerDateVal);
        buttonRemoveAccount = findViewById(R.id.button_myAccount_removeAccount);

        textViewEmail.setText(AccountEngine.getInstance().getUser().getEmail());
        textViewRegisterDate.setText(AccountEngine.getInstance().getUser().getRegisterDate().toString());
    }

}
