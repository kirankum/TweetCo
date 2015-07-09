package com.tweetco.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.imagedisplay.util.AsyncTask;
import com.imagedisplay.util.ImageFetcher;
import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.Exceptions.TweetNotFoundException;
import com.tweetco.activities.Constants;
import com.tweetco.activities.ListFragmentWithSwipeRefreshLayout;
import com.tweetco.activities.TweetDetailActivity;
import com.tweetco.activities.UserProfileActivity;
import com.tweetco.activities.adapter.LeaderboardAdapter;
import com.tweetco.activities.adapter.NotificationAdapter;
import com.tweetco.dao.LeaderboardUser;
import com.tweetco.dao.Notification;
import com.tweetco.dao.Tweet;
import com.tweetco.datastore.LeaderboardListSingleton;
import com.tweetco.datastore.NotificationsListSingleton;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.LeaderboardListModel;
import com.tweetco.models.NotificationsModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 08/07/15.
 */
public class NotificationsListFragment extends ListFragmentWithSwipeRefreshLayout implements OnChangeListener<List<Notification>>
{
    private NotificationAdapter mAdapter = null;
    private NotificationsModel model;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new NotificationsModel();
        this.setListAdapter(null);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        NotificationsListSingleton.INSTANCE.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        NotificationsListSingleton.INSTANCE.removeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if(NotificationsListSingleton.INSTANCE.getNotifications().isEmpty()) {
                        model.refreshNotificationsFromServer();
                    }
                    else {
                        refreshUiOnUIThread();
                    }
                }
                catch (MalformedURLException e)
                {

                }

                return null;
            }

        }.execute();

        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            model.refreshNotificationsFromServer();
                        } catch (MalformedURLException e) {

                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                }.execute();
            }
        });
    }

    @Override
    public void onChange(List<Notification> notifications) {

        refreshUiOnUIThread();
    }

    private void refreshUiOnUIThread() {
        if(isAdded()) {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter == null) {
                        mAdapter = new NotificationAdapter(NotificationsListFragment.this.getActivity(), android.R.layout.simple_list_item_1, NotificationsListSingleton.INSTANCE.getNotifications(), new NotificationAdapter.OnItemClick() {
                            @Override
                            public void onClick(int iterator) {
                                try {
                                    Tweet tweet = TweetsListSingleton.INSTANCE.getTweet(iterator);
                                    Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
                                    intent.putExtra("Tweet", tweet);
                                    getActivity().startActivity(intent);
                                } catch (TweetNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        NotificationsListFragment.this.setListAdapter(mAdapter);
                    }

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
