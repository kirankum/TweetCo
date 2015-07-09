package com.tweetco.activities.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.Exceptions.TweetNotFoundException;
import com.tweetco.TweetCo;
import com.tweetco.activities.Linkify;
import com.tweetco.activities.TweetDetailActivity;
import com.tweetco.dao.LeaderboardUser;
import com.tweetco.dao.Notification;
import com.tweetco.dao.Tweet;
import com.tweetco.datastore.TweetsListSingleton;
import com.tweetco.utility.UiUtility;

import java.util.List;

/**
 * Created by kirankumar on 08/07/15.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {

    public interface OnItemClick {
        public void onClick(int iterator);
    }

    private Context mContext = null;
    private OnItemClick mOnItemClick;
    public NotificationAdapter(Context context, int resource, List<Notification> objects, OnItemClick onItemClick) {
        super(context, resource, objects);
        mContext = context;
        mOnItemClick = onItemClick;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.notificationview, parent,false);
        }


        final Notification notification = (Notification)getItem(position);
        if(notification != null) {
            TextView notificationTextView = UiUtility.getView(convertView, R.id.notificationText);
            notificationTextView.setText(notification.notiftext);

            Linkify.addLinks(notificationTextView, Linkify.WEB_URLS | Linkify.HASH_TAGS | Linkify.USER_HANDLE);

            notificationTextView.setMovementMethod(new LinkMovementMethod());

            notificationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClick.onClick(notification.iterator);
                }
            });
        }

        return convertView;
    }
}
