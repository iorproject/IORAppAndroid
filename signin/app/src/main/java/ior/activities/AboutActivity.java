package ior.activities;

import android.graphics.Paint;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.samples.quickstart.signin.R;

import utils.IorUtils;

public class AboutActivity extends AppCompatActivity {

    private TextView textViewAboutTitle;
    private TextView textViewPrivacyTitle;
    private TextView textViewCreditsTitle;
    private NavigationView navigationView;
    private DrawerLayout mDraw;
    private ActionBarDrawerToggle mToggle;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textViewAboutTitle = findViewById(R.id.textViewAboutAppTitle_about);
        textViewPrivacyTitle = findViewById(R.id.textViewPrivacyPolicyTitle_about);
        textViewCreditsTitle = findViewById(R.id.textViewCreditsTitle_about);

        textViewAboutTitle.setPaintFlags(textViewAboutTitle.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        textViewPrivacyTitle.setPaintFlags(textViewPrivacyTitle.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        textViewCreditsTitle.setPaintFlags(textViewCreditsTitle.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);


        navigationView = findViewById(R.id.nav_view_top);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDraw = findViewById(R.id.drawer);
        mToggle = IorUtils.setNavigateBar(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("About");



    }

    public boolean onNavigationItemSelected(MenuItem item) {
        startActivity(IorUtils.getItemIntent(this, item.getItemId()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
        {
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDraw.closeDrawer(Gravity.LEFT);
    }
}
