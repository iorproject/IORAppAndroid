package ior.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.samples.quickstart.signin.R;

import ior.adapters.PageAdapter;
import ior.engine.ServerHandler;

public class MyReceiptsActivityNav extends AppCompatActivity {
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabLayout tabLayout;
    private BottomNavigationView navView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

        switch (item.getItemId()) {

            case R.id.navigation_myReceipts:
                break;

            case R.id.navigation_myPartners:
                ServerHandler.getInstance().fetchUserPartners(ServerHandler.getInstance().getUser().getEmail()
                , () -> {
                    Intent intent2 = new Intent(this, MyPartnersActivityNav.class);
                    startActivity(intent2);
                });
                break;

            case R.id.navigation_statInfo:
                return true;


        }
                //IorUtils.onNavigationItemSelected(getApplicationContext(), item, getSupportFragmentManager());
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_receipts_nav);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

    }

}
