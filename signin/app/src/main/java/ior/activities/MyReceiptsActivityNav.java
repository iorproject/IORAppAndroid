package ior.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.samples.quickstart.signin.R;

import java.util.stream.Collectors;

import ior.adapters.NavigatorAdapter;
import ior.adapters.PageAdapter;
import ior.engine.ServerHandler;
import ior.engine.User;
import utils.IorUtils;

public class MyReceiptsActivityNav extends AppCompatActivity {
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabLayout tabLayout;
    private DrawerLayout mDraw;
    private ActionBarDrawerToggle mToggle;
    private BottomNavigationView navViewBottom;
    private NavigatorAdapter navigatorAdapter;
    private NavigationView navigationView;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        email = getIntent().getStringExtra("email");
        initActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
        {
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//        return super.onContextItemSelected(item);
//    }

    public boolean onNavigationItemSelected(MenuItem item) {
        startActivity(IorUtils.getItemIntent(this, item.getItemId()));
        return true;
    }

    private void initActivity() {

        setContentView(R.layout.activity_my_receipts_nav);
        navViewBottom = findViewById(R.id.nav_view);
        navViewBottom.setOnNavigationItemSelectedListener(menuItem -> {
            IorUtils.onNavigationItemSelected(this, menuItem);
            return false;
        });


        viewPager = findViewById(R.id.viewPager_myReceipts);
        tabLayout = findViewById(R.id.tabLayout_receipts);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        AllReceiptsFragment allReceiptsFragment = new AllReceiptsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        allReceiptsFragment.setArguments(bundle);
        pageAdapter.addFragment(allReceiptsFragment);
        AdvancedSearchFragment advancedSearchFragment = new AdvancedSearchFragment();
        advancedSearchFragment.setArguments(bundle);
        pageAdapter.addFragment(advancedSearchFragment);
        viewPager.setAdapter(pageAdapter);


        String actionBarTitle = "My Receipts";
        User signInUser = ServerHandler.getInstance().getSignInUser();
        if (!email.equals(signInUser.getEmail())) {

            actionBarTitle = signInUser.getPartners().stream().
                    filter(p -> p.getEmail().equals(email)).collect(Collectors.toList()).get(0).getName() + "'s Receipts";

        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(actionBarTitle);
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.actionbar_background));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                viewPager.setCurrentItem(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mToggle = IorUtils.setNavigateBar(this);
        mDraw = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view_top);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        mDraw.closeDrawer(Gravity.LEFT);
        int x = 5;
    }
}
