package com.tweetco.activities.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagedisplay.util.Utils;
import com.onefortybytes.R;
import com.tweetco.dao.LeaderboardUser;
import com.tweetco.dao.Notification;
import com.tweetco.utility.UiUtility;

import java.util.List;

/**
 * Created by kirankumar on 08/07/15.
 */
public class NotificationAdapter extends ArrayAdapter<Notification> {

    private Context mContext = null;

    public NotificationAdapter(Context context, int resource, List<Notification> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.notificationview, parent,false);
        }


        Notification notification = (Notification)getItem(position);
        if(notification != null) {
            TextView notificationTextView = UiUtility.getView(convertView, R.id.notificationText);
            notificationTextView.setText(notification.notiftext);
        }

        return convertView;
    }
}
