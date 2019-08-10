package ior.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import java.text.DateFormat;
import java.util.Date;

import ior.engine.ServerHandler;
import utils.IorUtils;

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

        //textViewEmail.setText(ServerHandler.getInstance().getUser().getEmail());
        //textViewRegisterDate.setText(ServerHandler.getInstance().getUser().getRegisterDate().toString());
//        textViewEmail.setText(getSharedPreferences("ior.activities", Context.MODE_PRIVATE)
//                .getString("email", ""));

        textViewEmail.setText(ServerHandler.getInstance().getUser().getEmail());
        Date date = ServerHandler.getInstance().getUser().getRegisterDate();
        textViewRegisterDate.setText(IorUtils.dateToString(date));
    }

}
