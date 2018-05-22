package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class GDPRSetup implements Parcelable {

    private boolean mHasPaidVersion = false;
    private boolean mAllowNonPersonalisedForPaidVersion = false;
    private boolean mAllowNoConsent = false;
    private GDPRNetwork mAdNetworks[];

    private boolean mAskForAge = false;
    private boolean mNoToolbarTheme = false;
    private boolean mCheckRequestLocation = false;

    public GDPRSetup(GDPRNetwork... adNetworks) {
        if (adNetworks == null || adNetworks.length == 0) {
            throw new RuntimeException("At least one ad network must be provided, otherwise this setup does not make any sense.");
        }
        mAdNetworks = adNetworks;
    }

    public GDPRSetup withPaidVersion(boolean alsoProvideNonPersonalisedOption) {
        mHasPaidVersion = true;
        mAllowNonPersonalisedForPaidVersion = alsoProvideNonPersonalisedOption;
        return this;
    }

    public GDPRSetup withAllowNoConsent(boolean allowNoConsent) {
        mAllowNoConsent = allowNoConsent;
        return this;
    }

    public GDPRSetup withAskForAge(boolean askForAge) {
        mAskForAge = askForAge;
        return this;
    }

    public GDPRSetup withNoToolbarTheme(boolean noToolbarTheme) {
        mNoToolbarTheme = noToolbarTheme;
        return this;
    }

    public GDPRSetup withCheckRequestLocation(boolean checkRequestLocation) {
        mCheckRequestLocation = checkRequestLocation;
        return this;
    }

    // ----------------
    // Functions
    // ----------------

    public final String getNetworksCommaSeperated(Context context, boolean withLinks) {
        String networks = withLinks ? mAdNetworks[0].getHtmlLink() : mAdNetworks[0].getName();
        String innerSep = context.getString(R.string.gdpr_list_seperator);
        String lastSep = context.getString(R.string.gdpr_last_list_seperator);
        String sep;
        for (int i = 1; i < mAdNetworks.length; i++) {
            sep = i == mAdNetworks.length - 1 ? lastSep : innerSep;
            if (withLinks) {
                networks += sep + mAdNetworks[i].getHtmlLink();
            } else {
                networks += sep + mAdNetworks[i].getName();
            }
        }
        return networks;
    }

    public final boolean hasPaidVersion() {
        return mHasPaidVersion;
    }

    public final boolean allowNonPersonalisedForPaidVersion() {
        return mAllowNonPersonalisedForPaidVersion;
    }

    public final boolean allowNoConsent() {
        return mAllowNoConsent || mAllowNonPersonalisedForPaidVersion;
    }

    public final boolean askForAge() {
        return mAskForAge;
    }

    public final boolean noToolbarTheme() {
        return mNoToolbarTheme;
    }

    public final boolean checkRequestLocation() {
        return mCheckRequestLocation;
    }

    public final boolean containsAdNetwork() {
        for (GDPRNetwork network : mAdNetworks) {
            if (network.isAdNetwork()) {
                return true;
            }
        }
        return false;
    }

    // ----------------
    // Parcelable
    // ----------------

    public GDPRSetup(Parcel in) {
        mHasPaidVersion = in.readByte() == 1;
        mAllowNonPersonalisedForPaidVersion = in.readByte() == 1;
        mAllowNoConsent = in.readByte() == 1;
        Parcelable[] adNetworks = in.readParcelableArray(GDPRNetwork.class.getClassLoader());
        mAdNetworks = new GDPRNetwork[adNetworks.length];
        for (int i = 0; i < adNetworks.length; i++) {
            mAdNetworks[i] = (GDPRNetwork)adNetworks[i];
        }
        mAskForAge = in.readByte() == 1;
        mNoToolbarTheme = in.readByte() == 1;
        mCheckRequestLocation = in.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mHasPaidVersion ? (byte) 1 : 0);
        dest.writeInt(mAllowNonPersonalisedForPaidVersion ? (byte) 1 : 0);
        dest.writeInt(mAllowNoConsent ? (byte) 1 : 0);
        dest.writeParcelableArray(mAdNetworks, 0);
        dest.writeByte(mAskForAge ? (byte) 1 : 0);
        dest.writeByte(mNoToolbarTheme ? (byte) 1 : 0);
        dest.writeByte(mCheckRequestLocation ? (byte) 1 : 0);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GDPRSetup createFromParcel(Parcel in) {
            return new GDPRSetup(in);
        }

        public GDPRSetup[] newArray(int size) {
            return new GDPRSetup[size];
        }
    };
}
