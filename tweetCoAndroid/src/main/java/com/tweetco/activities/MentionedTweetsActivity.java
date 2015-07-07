package com.tweetco.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.onefortybytes.R;
import com.tweetco.activities.fragments.MentionedTweetsListFragment;
import com.tweetco.activities.fragments.TodayTweetsListFragment;
import com.tweetco.utility.UiUtility;

/**
 * Created by kirankumar on 07/07/15.
 */
public class MentionedTweetsActivity extends TweetCoBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mentionedtweetsactivity);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setHomeButtonEnabled(true);
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle("Mentioned Tweets");
        }

        if (UiUtility.getView(this, R.id.mentionedTweetsListFragmentContainer) != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            MentionedTweetsListFragment fragment = new MentionedTweetsListFragment();
            ft.replace(R.id.mentionedTweetsListFragmentContainer, fragment);
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
