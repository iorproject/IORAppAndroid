package ior.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.samples.quickstart.signin.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import utils.IorUtils;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/gmail.readonly")
                        , new Scope("https://www.googleapis.com/auth/gmail.labels")
                        , new Scope("https://www.googleapis.com/auth/gmail.modify"))
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();
        // [END configure_signin]

        IorUtils.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        String email = IorUtils.readFromFile(this);
        if (email.equals("")) {

            startActivity(new Intent(this, ServerAuthCodeActivity.class));
        }
        else {


            Intent intent = new Intent(this, HomeScreenActivity.class);
            intent.putExtra("email", email);
                startActivity(intent);

        }

//        String email = "";
//        try{
//
//            InputStream inputStream = getAssets().open(CURRENT_USER_FILE_NAME);
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            inputStream.read(buffer);
//            inputStream.close();
//            email = new String(buffer);
//            if (email.equals("")) {
//
//                startActivity(new Intent(this, ServerAuthCodeActivity.class));
//            }
//            else {
//
//
//                Intent intent = new Intent(this, HomeScreenActivity.class);
//                intent.putExtra("email", email);
//                startActivity(intent);
//
//            }
//        }
//        catch (Exception e){
//
//            int x = 4;
//        }


    }








}
