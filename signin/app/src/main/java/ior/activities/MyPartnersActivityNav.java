package ior.activities;

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
import android.view.WindowManager;

import com.google.samples.quickstart.signin.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import ior.adapters.PageAdapter;
import utils.IorUtils;

public class MyPartnersActivityNav extends AppCompatActivity {
    private BottomNavigationView navigationViewBottom;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabLayout tabLayout;
    private DrawerLayout mDraw;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationTopView;



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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mToggle = IorUtils.setNavigateBar(this);
        mDraw = findViewById(R.id.drawer);
        navigationTopView = findViewById(R.id.nav_view_top);
        navigationTopView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.tab2_background));
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        startActivity(IorUtils.getItemIntent(this, item.getItemId()));
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDraw.closeDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
        {
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

}
