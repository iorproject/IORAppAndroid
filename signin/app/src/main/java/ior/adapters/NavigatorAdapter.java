package ior.adapters;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.google.samples.quickstart.signin.R;

public class NavigatorAdapter {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Activity activity;

    public NavigatorAdapter(Activity i_activity, DrawerLayout drawer) {

        activity = i_activity;
        mDrawerLayout = drawer;;
        mToggle = new ActionBarDrawerToggle(activity, mDrawerLayout, R.string.open, R.string.close);
        mToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return mToggle.onOptionsItemSelected(item);
    }
}
