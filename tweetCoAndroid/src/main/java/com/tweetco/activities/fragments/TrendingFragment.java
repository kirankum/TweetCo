package com.tweetco.activities.fragments;

import java.net.MalformedURLException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.imagedisplay.util.AsyncTask;
import com.onefortybytes.R;
import com.tweetco.activities.Constants;
import com.tweetco.activities.ListFragmentWithSwipeRefreshLayout;
import com.tweetco.activities.TrendingFragmentActivity;
import com.tweetco.datastore.AccountSingleton;
import com.tweetco.datastore.TrendingListSingleton;
import com.tweetco.interfaces.OnChangeListener;
import com.tweetco.models.TrendingListModel;

public class TrendingFragment extends ListFragmentWithSwipeRefreshLayout implements OnChangeListener<TrendingListModel>
{



	public static class TrendingTag
	{
		public String hashtag;
		public String eventcount;
	}
		
	private TrendingAdapter mAdapter = null;

	private TrendingListModel model = null;
	
	public TrendingFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{

		super.onCreate(savedInstanceState);
		model = new TrendingListModel();
		this.setListAdapter(null);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		model.addListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		model.removeListener(this);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		TrendingTag tag = (TrendingTag)l.getItemAtPosition(position);
		
		if(tag!=null && !TextUtils.isEmpty(tag.hashtag))
		{
			Intent intent = new Intent(getActivity(), TrendingFragmentActivity.class);
			intent.putExtra(Constants.TREND_TAG_STR, tag.hashtag);
			startActivity(intent);
		}
		
		
	}
	


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params) {
				try {
					if(TrendingListSingleton.INSTANCE.getTrendingList() == null ||
							TrendingListSingleton.INSTANCE.getTrendingList().isEmpty()) {
						model.loadTrendingList();
					}
					else {
						refreshOnUIThread();
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
							model.refreshTrendingListFromServer();
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
	public void onChange(TrendingListModel model) {
		refreshOnUIThread();
	}

	private void refreshOnUIThread() {
		if(isAdded()) {
			this.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mAdapter == null) {
						mAdapter = new TrendingAdapter(TrendingFragment.this.getActivity(), android.R.layout.simple_list_item_1, TrendingListSingleton.INSTANCE.getTrendingList());

						TrendingFragment.this.setListAdapter(mAdapter);
					}

					mAdapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	private class TrendingAdapter extends ArrayAdapter<TrendingTag>
	{
        Context mContext = null;
		public TrendingAdapter(Context context, int resource, List<TrendingTag> list)
		{
			super(context, resource, list);
			mContext = context;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			if(convertView == null)
			{
				LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.trending, parent, false);
			}
			
			TrendingTag tag = getItem(position);
			if(tag!=null && !TextUtils.isEmpty(tag.hashtag))
			{
				TextView trend = (TextView)convertView.findViewById(R.id.trend);
				trend.setText("#" + tag.hashtag);
			}
			return convertView;
		}	
	}
}
