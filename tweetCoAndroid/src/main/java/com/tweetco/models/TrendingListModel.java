package com.tweetco.models;

import com.tweetco.activities.TrendingFragment;
import com.tweetco.clients.TrendingListClient;
import com.tweetco.database.dao.Account;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.datastore.TrendingListSingleton;
import com.tweetco.interfaces.SimpleObservable;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by kirankumar on 24/06/15.
 */
public class TrendingListModel extends SimpleObservable<TrendingListModel> {

    private TrendingListClient client = new TrendingListClient();

    public void loadTrendingList() throws MalformedURLException {
        if(TrendingListSingleton.INSTANCE.getTrendingList().isEmpty())
        {
            refreshTrendingListFromServer();
        }
        else
        {
            notifyObservers(this);
        }
    }

    public void refreshTrendingListFromServer() throws MalformedURLException
    {
        Account account = AccountSingleton.INSTANCE.getAccountModel().getAccountCopy();

        List<TrendingFragment.TrendingTag> tempTrendingList = client.getTrendingList();

        TrendingListSingleton.INSTANCE.updateTrendingListFromServer(tempTrendingList);

        notifyObservers(this);
    }

}
