package ior.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.samples.quickstart.signin.R;

public class ReceiptsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SectionsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager_receipts);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager = findViewById(R.id.viewPager_receipts);
        initViewPager();

    }

    private void initViewPager() {


        pagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager() );
        pagerAdapter.addFragment(new AllReceiptsFragment(), "All Receipts");
        pagerAdapter.addFragment(new AdvancedSearchFragment(), "Advanced Search");

        viewPager.setAdapter(pagerAdapter);


    }
}