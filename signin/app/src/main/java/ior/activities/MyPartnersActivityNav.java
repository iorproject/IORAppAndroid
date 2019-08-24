package ior.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.samples.quickstart.signin.R;

import ior.adapters.PageAdapter;
import utils.IorUtils;

public class MyPartnersActivityNav extends AppCompatActivity {
    private BottomNavigationView navigationViewBottom;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_partners_nav);
        navigationViewBottom = findViewById(R.id.nav_view);
        navigationViewBottom.setSelectedItemId(R.id.navigation_myPartners);
        navigationViewBottom.setOnNavigationItemSelectedListener(menuItem -> {
            IorUtils.onNavigationItemSelected(this, menuItem);
            return false;
        });
        viewPager = findViewById(R.id.viewPager_myPartners);
        tabLayout = findViewById(R.id.tabLayout_myPartners);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(new MyPartnersFragment());
        pageAdapter.addFragment(new RequestFriendshipFragment());
        pageAdapter.addFragment(new FollowersFragment());
        //pageAdapter.addFragment(new ShareRequestsFragment());
        viewPager.setAdapter(pageAdapter);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


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
