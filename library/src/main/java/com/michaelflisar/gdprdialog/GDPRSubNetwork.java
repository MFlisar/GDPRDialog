package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class GDPRSubNetwork implements Parcelable {
    private String mName;
    private String mLink;

    public GDPRSubNetwork(Context context, int name, int link) {
        mName = context.getString(name);
        mLink = context.getString(link);
    }

    public GDPRSubNetwork(String name, String link) {
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

    public GDPRSubNetwork(Parcel in) {
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

    public static final Creator CREATOR = new Creator() {
        public GDPRSubNetwork createFromParcel(Parcel in) {
            return new GDPRSubNetwork(in);
        }

        public GDPRSubNetwork[] newArray(int size) {
            return new GDPRSubNetwork[size];
        }
    };
}