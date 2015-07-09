package com.tweetco.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.onefortybytes.R;
import com.tweetco.activities.fragments.NotificationsListFragment;
import com.tweetco.activities.fragments.TodayTweetsListFragment;
import com.tweetco.utility.UiUtility;

/**
 * Created by kirankumar on 08/07/15.
 */
public class NotificationsActivity extends TweetCoBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationsactivity);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle("Notifications");
        }

        if (UiUtility.getView(this, R.id.notificationsListFragmentContainer) != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            NotificationsListFragment fragment = new NotificationsListFragment();
            ft.replace(R.id.notificationsListFragmentContainer, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
