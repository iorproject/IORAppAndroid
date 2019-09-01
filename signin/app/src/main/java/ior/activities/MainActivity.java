package ior.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.samples.quickstart.signin.R;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import ior.engine.ServerHandler;
import utils.IorUtils;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RoundedHorizontalProgressBar progressBarFetchingData;
    private TextView textViewProgress;
    private TextView textViewMessage;
    private Dialog dialogError;


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


        SharedPreferences sharedPref = getSharedPreferences("ior.activities" ,Context.MODE_PRIVATE);
        String email = sharedPref.getString("email", "");
        //String email = IorUtils.readFromFile(this);
        progressBar = findViewById(R.id.progressBar);
        textViewProgress = findViewById(R.id.textViewProgress_main);
        textViewMessage = findViewById(R.id.textView_loading_main);
        getSupportActionBar().hide();
        initProgressBar();
        initDialog();


        if (email.equals("")) {

            startActivity(new Intent(this, ServerAuthCodeActivity.class));
        }
        else {
            ServerHandler.getInstance().setOnProgressFetchingData(this::updateProgressBar);
            ServerHandler.getInstance().fetchUserInfo(email, () -> {
                        Intent intent = new Intent(this, MyReceiptsActivityNav.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    },

                    (msg) ->  runOnUiThread(() -> {

                        TextView tvMsg = dialogError.findViewById(R.id.tv_message_serverErrorDialog);
                        tvMsg.setText(msg + "\nPlease try again later.");
                        dialogError.show();

                    }));

            ServerHandler.getInstance().fetchUserPartners(email, null, null);
            IorUtils.setDefultBitmapImage(this);

            progressBar.getIndeterminateDrawable().setColorFilter(0xFF303F9F, android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }

    private void initProgressBar() {

        progressBarFetchingData = findViewById(R.id.progressBarFetchingData);
        progressBarFetchingData.setMax(100);
        progressBarFetchingData.setProgress(0);


        Animation fadingOut = new AlphaAnimation(1.0f, 0.0f);
        fadingOut.setDuration(1500);
        fadingOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textViewProgress.setAlpha(1.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        textViewMessage.setAnimation(fadingOut);
        fadingOut.setRepeatCount(AlphaAnimation.INFINITE);
        fadingOut.start();

    }

    private void updateProgressBar() {

        int updateProg = progressBarFetchingData.getProgress() + 20;
        progressBarFetchingData.setProgress(updateProg);
        textViewProgress.setText(updateProg + " %");
    }

    private void initDialog() {

        dialogError = new Dialog(this);
        dialogError.setContentView(R.layout.server_error_dialog);
        dialogError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogError.setCanceledOnTouchOutside(false);

        Button bt_ok = dialogError.findViewById(R.id.bt_Ok_serverErrorDialog);

        bt_ok.setOnClickListener(v -> {

            finish();
            System.exit(0);
        });
    }
}