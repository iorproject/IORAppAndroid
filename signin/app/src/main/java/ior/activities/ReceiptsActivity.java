package ior.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.samples.quickstart.signin.R;

import ior.adapters.NavigatorAdapter;

public class ReceiptsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SectionsPagerAdapter pagerAdapter;
    private NavigatorAdapter navigatorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager_receipts);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.tab2_background));
        viewPager = findViewById(R.id.viewPager_receipts);
        initViewPager();

    }

    private void initViewPager() {


        pagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager() );
        pagerAdapter.addFragment(new AllReceiptsFragment(), "All Receipt");
        pagerAdapter.addFragment(new AdvancedSearchFragment(), "Advanced Search");

        viewPager.setAdapter(pagerAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return navigatorAdapter.onOptionsItemSelected( item);
    }
}