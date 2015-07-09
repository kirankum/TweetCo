package com.tweetco.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kirankumar on 08/07/15.
 */
public class Notification implements Parcelable {

    public String notiftext;
    public int iterator;

    protected Notification(Parcel parcel) {
        notiftext = parcel.readString();
        iterator = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(notiftext);
        parcel.writeInt(iterator);
    }
}
