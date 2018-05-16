package com.michaelflisar.gdprdialog;

import android.os.Parcel;
import android.os.Parcelable;

public class GDPRSetup implements Parcelable {
    private boolean mAllowUsageWithoutConsent = false;
    private GDPRNetwork mAdNetworks[] = null;

    public GDPRSetup(GDPRNetwork... adNetworks) {
        if (adNetworks == null || adNetworks.length == 0) {
            throw new RuntimeException("At least one ad network must be provided, otherwise this setup does not make any sense.");
        }
        mAdNetworks = adNetworks;
    }

    public GDPRSetup withAllowUsageWithoutConsent(boolean allowUsageWithoutConsent) {
        mAllowUsageWithoutConsent = allowUsageWithoutConsent;
        return this;
    }

    // ----------------
    // Functions
    // ----------------

    public String getNetworksCommaSeperated(boolean withLinks) {
        String networks = withLinks ? mAdNetworks[0].getHtmlLink() : mAdNetworks[0].getName();
        for (int i = 1; i < mAdNetworks.length; i++) {
            if (withLinks) {
                networks += ", " + mAdNetworks[i].getHtmlLink();
            } else {
                networks += ", " + mAdNetworks[i].getName();
            }
        }
        return networks;
    }

    public boolean isAllowUsageWithoutConsent() {
        return mAllowUsageWithoutConsent;
    }

    // ----------------
    // Parcelable
    // ----------------

    public GDPRSetup(Parcel in) {
        mAllowUsageWithoutConsent = in.readByte() == 1;
        mAdNetworks = (GDPRNetwork[]) in.readParcelableArray(GDPRNetwork.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mAllowUsageWithoutConsent ? (byte) 1 : 0);
        dest.writeParcelableArray(mAdNetworks, 0);
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
