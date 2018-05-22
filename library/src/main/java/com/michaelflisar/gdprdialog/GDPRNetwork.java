package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class GDPRNetwork implements Parcelable {
    private String mName;
    private String mLink;
    private boolean mIsCollection;
    private boolean mIsAdNetwork;

    public GDPRNetwork(Context context, int name, int link, boolean isCollection, boolean isAdNetwork) {
        mName = context.getString(name);
        mLink = context.getString(link);
        mIsCollection = isCollection;
        mIsAdNetwork = isAdNetwork;
    }

    public GDPRNetwork(String name, String link, boolean isCollection, boolean isAdNetwork) {
        mName = name;
        mLink = link;
        mIsCollection = isCollection;
        mIsAdNetwork = isAdNetwork;
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

    public boolean isCollection() {
        return mIsCollection;
    }

    public boolean isAdNetwork() {
        return mIsAdNetwork;
    }

    public String getHtmlLink() {
        return "<a href=\"" + mLink + "\">" + mName + "</a>";
    }

    public String getCheckboxHtmlLink(Context context) {
        return mName + " (<a href=\"" + mLink + "\">" + context.getString(R.string.gdpr_link) + "</a>)";
    }

    // ----------------
    // Parcelable
    // ----------------

    public GDPRNetwork(Parcel in) {
        mName = in.readString();
        mLink = in.readString();
        mIsCollection = in.readByte() == 1;
        mIsAdNetwork = in.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mLink);
        dest.writeByte(mIsCollection ? (byte)1 : 0);
        dest.writeByte(mIsAdNetwork ? (byte)1 : 0);
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