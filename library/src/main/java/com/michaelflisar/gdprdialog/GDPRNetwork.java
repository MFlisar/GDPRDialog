package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.michaelflisar.gdprdialog.helper.GDPRUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GDPRNetwork implements Parcelable {
    private String mName;
    private String mLink;
    private String mType;
    private boolean mIsCollection;
    private boolean mIsAdNetwork;
    private ArrayList<GDPRSubNetwork> mSubNetworks;

    public GDPRNetwork(Context context, int name, int link, int type, boolean isCollection, boolean isAdNetwork) {
        mName = context.getString(name);
        mLink = context.getString(link);
        mType = context.getString(type);
        mIsCollection = isCollection;
        mIsAdNetwork = isAdNetwork;
        mSubNetworks = new ArrayList<>();
    }

    public GDPRNetwork(String name, String link, String type, boolean isCollection, boolean isAdNetwork) {
        mName = name;
        mLink = link;
        mType = type;
        mIsCollection = isCollection;
        mIsAdNetwork = isAdNetwork;
        mSubNetworks = new ArrayList<>();
    }

    public GDPRNetwork addSubNetwork(GDPRSubNetwork network) {
        mSubNetworks.add(network);
        return this;
    }

    public GDPRNetwork addSubNetworks(List<GDPRSubNetwork> networks) {
        mSubNetworks.addAll(networks);
        return this;
    }

    public GDPRNetwork copy() {
        return new GDPRNetwork(mName, mLink, mType, mIsCollection, mIsAdNetwork)
                .addSubNetworks(mSubNetworks);
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

    public String getType() {
        return mType;
    }

    public boolean isCollection() {
        return mIsCollection;
    }

    public boolean isAdNetwork() {
        return mIsAdNetwork;
    }

    public ArrayList<GDPRSubNetwork> getSubNetworks() {
        return mSubNetworks;
    }

    public String getHtmlLink(Context context, boolean withSubNetworks) {
        String link = "<a href=\"" + mLink + "\">" + mName + "</a>";
        if (withSubNetworks && mSubNetworks.size() > 0) {
            link += " (";
            List<String> values = new ArrayList<>();
            for (GDPRSubNetwork subNetwork : mSubNetworks) {
                values.add(subNetwork.getHtmlLink());
            }
            link += GDPRUtils.getCommaSeperatedString(context, values);
            link += ")";
        }
        return link;
    }

    @Override
    public String toString() {
        String s = mName;
        s += " [";
        List<String> values = new ArrayList<>();
        for (GDPRSubNetwork subNetwork : mSubNetworks) {
            values.add(subNetwork.getName());
        }
        s += TextUtils.join(",", values);
        s += "]";
        return s;
    }

    // ----------------
    // Parcelable
    // ----------------

    public GDPRNetwork(Parcel in) {
        mName = in.readString();
        mLink = in.readString();
        mType = in.readString();
        mIsCollection = in.readByte() == 1;
        mIsAdNetwork = in.readByte() == 1;
        mSubNetworks = new ArrayList<>();
        int subNetworks = in.readInt();
        while (subNetworks > 0) {
            mSubNetworks.add(in.readParcelable(GDPRSubNetwork.class.getClassLoader()));
            subNetworks--;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mLink);
        dest.writeString(mType);
        dest.writeByte(mIsCollection ? (byte) 1 : 0);
        dest.writeByte(mIsAdNetwork ? (byte) 1 : 0);
        dest.writeInt(mSubNetworks.size());
        for (GDPRSubNetwork subNetwork : mSubNetworks) {
            dest.writeParcelable(subNetwork, 0);
        }
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