package ior.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.samples.quickstart.signin.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import ior.engine.ServerHandler;
import utils.IorUtils;
import utils.ParameterStringBuilder;

import static com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR;

/**
 * Demonstrates retrieving an offline access one-time code for the current Google user, which
 * can be exchanged by your server for an access token and refresh token.
 */
public class ServerAuthCodeActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String TAG = "ServerAuthCodeActivity";
    private static final int RC_GET_AUTH_CODE = 9003;

    //private GoogleSignInClient mGoogleSignInClient;
    //private TextView mAuthCodeTextView;
    private String email = null;
    private String name = "";
    private String access_token = null;
    private String refresh_token = null;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        // Views
        //mAuthCodeTextView = findViewById(R.id.detail);

        // Button click listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        initStartTimeDialog();

        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();

        // [START configure_signin]
        // Configure sign-in to request offline access to the user's ID, basic
        // profile, and Google Drive. The first time you request a code you will
        // be able to exchange it for an access token and refresh token, which
        // you should store. In subsequent calls, the code will only result in
        // an access token. By asking for profile access (through
        // DEFAULT_SIGN_IN) you will also get an ID Token as a result of the
        // code exchange.
//        String serverClientId = getString(R.string.server_client_id);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope("https://www.googleapis.com/auth/gmail.readonly")
//                , new Scope("https://www.googleapis.com/auth/gmail.labels")
//                , new Scope("https://www.googleapis.com/auth/gmail.modify"))
//                .requestServerAuthCode(serverClientId)
//                .requestEmail()
//                .build();
//        // [END configure_signin]
//
//        IorUtils.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void getAuthCode() {
        // Start the retrieval process for a server auth code.  If requested, ask for a refresh
        // token.  Otherwise, only get an access token if a refresh token has been previously
        // retrieved.  Getting a new access token for an existing grant does not require
        // user consent.
        Intent signInIntent = IorUtils.mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_AUTH_CODE);
    }

    private void signOut() {
        IorUtils.mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    public void revokeAccess() {
        IorUtils.mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_AUTH_CODE) {
            // [START get_auth_code]
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String authCode = account.getServerAuthCode();

                this.email = account.getEmail();
                name = account.getDisplayName();

                // Show signed-un UI
                updateUI(account);
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("grant_type", "authorization_code")
                        .add("client_id", getString(R.string.server_client_id))
                        .add("client_secret", getString(R.string.server_secret_id))
                        .add("redirect_uri","")
                        .add("code",authCode)
                        .build();
                final Request request = new Request.Builder()
                        .url("https://www.googleapis.com/oauth2/v4/token")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(final Request request, final IOException e) {
                        Log.e("Omer", e.toString());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            runOnUiThread(() -> registerUser(jsonObject));
                            final String message = jsonObject.toString();
                            Log.i("Omerr", message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //IorUtils.writeToSharePreference(getParent(), "email", email);
                            //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });
                // TODO(developer): send code to server and exchange for access/refresh/ID tokens
            } catch (ApiException e) {
                Log.w(TAG, "Sign-in failed", e);
                String message = "";
                if (e.getStatusCode() == NETWORK_ERROR)
                    message = "The connection to Google has failed. \nPlease try again later.";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                updateUI(null);
            }
            // [END get_auth_code]
        }
    }

    private void registerUser(JSONObject jsonObject) {

        IorUtils.writeToSharePreference(this, "email", email);

        try {
            access_token = jsonObject.getString("access_token");
            refresh_token = jsonObject.getString("refresh_token");
            dialog.show();

        }
        catch (JSONException e) {

            if (refresh_token == null)
                startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void updateDialogResult(View v) {

        dialog.dismiss();

        Calendar cal = Calendar.getInstance();

        switch (((Button)v).getText().toString()) {

            case "Month":
                cal.add(Calendar.MONTH, -1);
                break;

            case "Week":
                cal.add(Calendar.DAY_OF_WEEK, -7);
                break;
        }

        ServerHandler.getInstance().registerUser(email, name,  access_token, refresh_token, cal.getTime(),
                () -> startActivity(new Intent(this, MainActivity.class)));

    }

    private void updateUI(@Nullable GoogleSignInAccount account) {

    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                getAuthCode();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    private void initStartTimeDialog() {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.scanning_email_permission);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Button bt_month = dialog.findViewById(R.id.bt_month_scanningPermission);
        Button bt_week = dialog.findViewById(R.id.bt_week_scanningPermission);
        Button bt_now = dialog.findViewById(R.id.bt_now_scanningPermission);

        bt_month.setOnClickListener(this::updateDialogResult);
        bt_week.setOnClickListener(this::updateDialogResult);
        bt_now.setOnClickListener(this::updateDialogResult);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
