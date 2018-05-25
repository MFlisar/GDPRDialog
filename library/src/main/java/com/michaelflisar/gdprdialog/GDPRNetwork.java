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
    private boolean mIsIntermediatorWithSubNetworks;
    private boolean mIsAdNetwork;
    private ArrayList<GDPRSubNetwork> mSubNetworks;
    private String mSubNetworksLink;

    public GDPRNetwork(Context context, String name, String link, int type, boolean isAdNetwork) {
        mName = name;
        mLink = link;
        mType = context.getString(type);
        mIsIntermediatorWithSubNetworks = false;
        mSubNetworksLink = null;
        mIsAdNetwork = isAdNetwork;
        mSubNetworks = new ArrayList<>();
    }

    public GDPRNetwork(String name, String link, String type, boolean isAdNetwork) {
        mName = name;
        mLink = link;
        mType = type;
        mIsIntermediatorWithSubNetworks = false;
        mSubNetworksLink = null;
        mIsAdNetwork = isAdNetwork;
        mSubNetworks = new ArrayList<>();
    }

    public GDPRNetwork withIsIntermediator(String subNetworksLink) {
        mIsIntermediatorWithSubNetworks = true;
        mSubNetworksLink = subNetworksLink;
        return this;
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
        GDPRNetwork copy = new GDPRNetwork(mName, mLink, mType, mIsAdNetwork)
                .addSubNetworks(mSubNetworks);
        copy.mIsIntermediatorWithSubNetworks = this.mIsIntermediatorWithSubNetworks;
        copy.mSubNetworksLink = this.mSubNetworksLink;
        return copy;
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

    public boolean isIntermediatorWithSubNetworks() {
        return mIsIntermediatorWithSubNetworks;
    }

    public boolean isAdNetwork() {
        return mIsAdNetwork;
    }

    public ArrayList<GDPRSubNetwork> getSubNetworks() {
        return mSubNetworks;
    }

    public String getHtmlLink(Context context, boolean withIntermediatorLink, boolean withSubNetworks) {
        String link = "<a href=\"" + mLink + "\">" + mName + "</a>";
        if (withIntermediatorLink && mIsIntermediatorWithSubNetworks && mSubNetworksLink != null) {
            link += " (<a href=\"" + mSubNetworksLink + "\">" + context.getString(R.string.gdpr_show_me_partners) + "</a>)";
        }
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
        mIsIntermediatorWithSubNetworks = in.readByte() == 1;
        mIsAdNetwork = in.readByte() == 1;
        mSubNetworks = new ArrayList<>();
        int subNetworks = in.readInt();
        while (subNetworks > 0) {
            mSubNetworks.add(in.readParcelable(GDPRSubNetwork.class.getClassLoader()));
            subNetworks--;
        }
        mSubNetworksLink = in.readString();
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
        dest.writeByte(mIsIntermediatorWithSubNetworks ? (byte) 1 : 0);
        dest.writeByte(mIsAdNetwork ? (byte) 1 : 0);
        dest.writeInt(mSubNetworks.size());
        for (GDPRSubNetwork subNetwork : mSubNetworks) {
            dest.writeParcelable(subNetwork, 0);
        }
        dest.writeString(mSubNetworksLink);
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