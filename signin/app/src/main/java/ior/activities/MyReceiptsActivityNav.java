package ior.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.samples.quickstart.signin.R;

import ior.adapters.NavigatorAdapter;
import ior.adapters.PageAdapter;
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


//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = item -> {
//
//        switch (item.getItemId()) {
//
//            case R.id.navigation_myReceipts:
//                break;
//
//            case R.id.navigation_myPartners:
//                ServerHandler.getInstance().fetchUserPartners(ServerHandler.getInstance().getSignInUser().getEmail(),
//                () -> {
//                    Intent intent2 = new Intent(this, MyPartnersActivityNav.class);
//                    startActivity(intent2);
//                });
//                break;
//
//            case R.id.navigation_statInfo:
//                IorUtils.signOut(this);
//                return true;
//
//
//        }
//                //IorUtils.onNavigationItemSelected(getApplicationContext(), item, getSupportFragmentManager());
//                return false;
//            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            return IorUtils.onNavigationItemSelected(this, menuItem);
        });


        viewPager = findViewById(R.id.viewPager_myReceipts);
        tabLayout = findViewById(R.id.tabLayout_receipts);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new AllReceiptsFragment());
        pageAdapter.addFragment(new AdvancedSearchFragment());
        viewPager.setAdapter(pageAdapter);


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
        navigationView = findViewById(R.id.nav_view_top);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onStart() {
        super.onStart();

        int x = 5;
    }
}
