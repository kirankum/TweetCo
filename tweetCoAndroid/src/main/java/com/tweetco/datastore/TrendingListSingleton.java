package com.tweetco.datastore;

import com.tweetco.activities.fragments.TrendingFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirankumar on 24/06/15.
 */
public enum TrendingListSingleton {
    INSTANCE;

    private List<TrendingFragment.TrendingTag> trendingList = new ArrayList<TrendingFragment.TrendingTag>();

    public List<TrendingFragment.TrendingTag> getTrendingList()
    {
        return trendingList;
    }

    public void updateTrendingListFromServer(List<TrendingFragment.TrendingTag> list)
    {
        trendingList.removeAll(list);

        trendingList.addAll(list);

        trendingList.retainAll(list);
    }
}
