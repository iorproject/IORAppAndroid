package utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.samples.quickstart.signin.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ior.activities.HomeScreenActivity;
import ior.activities.MainActivity;
import ior.activities.MyPartnersActivityNav;
import ior.activities.MyReceiptsActivityNav;
import ior.activities.ServerAuthCodeActivity;
import ior.activities.ViewStatisticsActivity;
import ior.engine.ServerHandler;
import ior.activities.ShowProfileActivity;

public class IorUtils {

    public final static String CURRENT_USER_FILE_NAME = "current_user.txt";

    public static GoogleSignInClient mGoogleSignInClient;

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

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
        return toggle;
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
        }

        return result;
    }

    public static boolean onNavigationItemSelected(Activity activity, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.navigation_myReceipts:
                if (activity.getClass() != MyReceiptsActivityNav.class) {
                    ServerHandler.getInstance().fetchUserInfo(ServerHandler.getInstance().getSignInUser().getEmail(),
                            () -> {
                                Intent intent = new Intent(activity, MyReceiptsActivityNav.class);
                                activity.startActivity(intent);
                            });
                    return true;
                }
                break;


            case R.id.navigation_myPartners:
                if (activity.getClass() != MyPartnersActivityNav.class) {
                    ServerHandler.getInstance().fetchUserPartners(ServerHandler.getInstance().getSignInUser().getEmail(),
                            () -> {
                                Intent intent = new Intent(activity, MyPartnersActivityNav.class);
                                activity.startActivity(intent);
                            });
                    return true;
                }

                break;

            case R.id.navigation_statInfo:
                if (activity.getClass() != ViewStatisticsActivity.class) {
                    Intent intent = new Intent(activity, ViewStatisticsActivity.class);
                    intent.putExtra("email",ServerHandler.getInstance().getSignInUser().getEmail());
                    activity.startActivity(intent);
                    return true;
                }

                break;


        }

        return false;
    }
}
