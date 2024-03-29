package utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.samples.quickstart.signin.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ior.activities.AboutActivity;
import ior.activities.HomeScreenActivity;
import ior.activities.MainActivity;
import ior.activities.MyPartnersActivityNav;
import ior.activities.MyReceiptsActivityNav;
import ior.activities.ServerAuthCodeActivity;
import ior.activities.ViewStatisticsActivity;
import ior.engine.ServerHandler;
import ior.activities.ShowProfileActivity;
import ior.engine.User;

import static android.support.v4.content.ContextCompat.getSystemService;

public class IorUtils {

    public final static String CURRENT_USER_FILE_NAME = "current_user.txt";

    public static GoogleSignInClient mGoogleSignInClient;

    private static Bitmap defultProfileImage;

    public static void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(
                    CURRENT_USER_FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {

        }
    }


    public static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(CURRENT_USER_FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void signOut(Activity activity) {

        writeToSharePreference(activity, "email", "");
        ServerHandler.getInstance().reset();

        mGoogleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //writeToFile("", activity);
                //writeToSharePreference(activity, "email", "");
                //ServerHandler.getInstance().reset();
                //activity.startActivity(new Intent(activity, ServerAuthCodeActivity.class));

            }
        });
    }

    public static void revokeAccess(Activity activity) {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        writeToSharePreference(activity, "email", "");
                        activity.startActivity(new Intent(activity, ServerAuthCodeActivity.class));
                    }
                });


    }

    public static void writeToSharePreference(Activity activity, String key, String val) {

        SharedPreferences sharedPref = activity.getSharedPreferences("ior.activities", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public static String dateToString(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        String dateStr = dateFormat.format(date);
        return dateStr;

    }

    public static void onNavigationItemSelected(Context activity, MenuItem item, FragmentManager fm) {


        switch (item.getItemId()) {

            case R.id.navigation_myReceipts:
                Intent intent1 = new Intent(activity, MyReceiptsActivityNav.class);
                activity.startActivity(intent1);
                break;

            case R.id.navigation_myPartners:
                Intent intent2 = new Intent(activity, HomeScreenActivity.class);
                activity.startActivity(intent2);
                break;


        }
    }

    public static ActionBarDrawerToggle setNavigateBar(Activity activity) {
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        initNavigateDataHeader(activity);
        return toggle;
    }

    private static void initNavigateDataHeader(Activity activity)
    {
        NavigationView navigationView = activity.findViewById(R.id.nav_view_top);
        View header=navigationView.getHeaderView(0);
        User signInuser = ServerHandler.getInstance().getSignInUser();
        CircularImageView profileImage = header.findViewById(R.id.profile_image_header);
        profileImage.setImageBitmap(signInuser.getProfileImage());
        TextView name = header.findViewById(R.id.profile_name_header);
        name.setText(signInuser.getName());
        TextView email = header.findViewById(R.id.profile_email_header);
        email.setText(signInuser.getEmail());
    }

    public static Intent getItemIntent(Activity activity, int itemId) {
        Intent result = null;
        switch (itemId) {
            case R.id.profile:
                result = new Intent(activity, ShowProfileActivity.class);
                break;

            case R.id.signOut_menu:
                IorUtils.signOut(activity);
                result = new Intent(activity, MainActivity.class);
                break;

            case R.id.about:
                result = new Intent(activity, AboutActivity.class);
                break;
        }

        return result;
    }

    public static String getStringFromBitmap(Bitmap bitmap)
    {
        String image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return image;
    }

    public static Bitmap getBitmapFromString(String image)
    {
        if (image == null)
        {
            return null;
        }

        try {
            byte [] encodeByte = Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch (Exception e)
        {
            return null;
        }


    }
    public static void setDefultBitmapImage(Context context)
    {
        defultProfileImage= BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_profile);
    }

    public static Bitmap getDefultProfileImage()
    {
        return defultProfileImage;
    }

    public static boolean onNavigationItemSelected(Activity activity, MenuItem item) {

        Dialog dialogError = getServerErrorDialog(activity);

        switch (item.getItemId()) {

            case R.id.navigation_myReceipts:
                ProgressDialog dialog = new ProgressDialog(activity);
                IorUtils.showProgressDialog(dialog, activity);
                ServerHandler.getInstance().fetchUserAllReceipts(ServerHandler.getInstance().getSignInUser().getEmail(),
                        () -> {
                            dialog.dismiss();
                            Intent intent = new Intent(activity, MyReceiptsActivityNav.class);
                            intent.putExtra("email", ServerHandler.getInstance().getSignInUser().getEmail());
                            activity.startActivity(intent);
                        }, (msg) ->  activity.runOnUiThread(() -> {

                TextView tvMsg = dialogError.findViewById(R.id.tv_message_serverErrorDialog);
                tvMsg.setText(msg + "Please try again later.");
                dialogError.show();

            }));

                break;

            case R.id.navigation_myPartners:
                ProgressDialog dialog2 = new ProgressDialog(activity);
                IorUtils.showProgressDialog(dialog2, activity);
                ServerHandler.getInstance().fetchUserPartners(ServerHandler.getInstance().getSignInUser().getEmail(),
                        () -> {
                            dialog2.dismiss();
                            Intent intent = new Intent(activity, MyPartnersActivityNav.class);
                            activity.startActivity(intent);
                        },
                        (msg) ->  activity.runOnUiThread(() -> {

                            TextView tvMsg = dialogError.findViewById(R.id.tv_message_serverErrorDialog);
                            tvMsg.setText(msg + "Please try again later.");
                            dialogError.show();

                        }));
                break;

            case R.id.navigation_statInfo:
                Intent intent = new Intent(activity, ViewStatisticsActivity.class);
                intent.putExtra("email",ServerHandler.getInstance().getSignInUser().getEmail());
                activity.startActivity(intent);

                break;
        }

        return false;
    }

    public static String firstUpperCase(String str) {

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void showProgressDialog(ProgressDialog dialog, Context context) {

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress_bar_loading_data));
        dialog.setCanceledOnTouchOutside(false);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        dialog.setCanceledOnTouchOutside(false);



        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int wid = display.getWidth();

        InsetDrawable inset = new InsetDrawable(back, wid / 3 + (dialog.getWindow().getDecorView().getWidth() / 2) );
        dialog.getWindow().setBackgroundDrawable(inset);

        dialog.show();
    }


    public static Dialog getServerErrorDialog(Context context) {

        Dialog dialogError = new Dialog(context);
        dialogError.setContentView(R.layout.server_error_dialog);
        dialogError.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogError.setCanceledOnTouchOutside(false);

        Button bt_ok = dialogError.findViewById(R.id.bt_Ok_serverErrorDialog);

        bt_ok.setOnClickListener(v -> {

            dialogError.dismiss();
        });

        return  dialogError;

    }

    public static List<User> filterUserStarWith(List<User> origin, String start)
    {
        return origin.stream().filter((user) -> user.getName().toUpperCase().startsWith(start.toUpperCase())).collect(Collectors.toList());
    }

}