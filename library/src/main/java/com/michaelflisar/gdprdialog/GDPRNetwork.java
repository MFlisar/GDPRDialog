package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class GDPRNetwork implements Parcelable {
    private String mName;
    private String mLink;

    public GDPRNetwork(Context context, int name, int link) {
        mName = context.getString(name);
        mLink = context.getString(link);
    }

    public GDPRNetwork(String name, String link) {
        mName = name;
        mLink = link;
    }

    // ----------------
    // Functions
    // ----------------

    public String getName() {
        return mName;
    }

    public String getLink() {
        return mLink;
    }

    public String getHtmlLink() {
        return "<a href=\"" + mLink + "\">" + mName + "</a>";
    }

    // ----------------
    // Parcelable
    // ----------------

    public GDPRNetwork(Parcel in) {
        mName = in.readString();
        mLink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mLink);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GDPRNetwork createFromParcel(Parcel in) {
            return new GDPRNetwork(in);
        }

        public GDPRNetwork[] newArray(int size) {
            return new GDPRNetwork[size];
        }
    };
}