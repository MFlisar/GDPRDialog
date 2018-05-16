package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class GDPRSetup implements Parcelable {
    private boolean mAllowUsageWithoutConsent = false;
    private List<String> mAdNetworks = new ArrayList<>();

    public GDPRSetup(Context context, int... adNetwork) {
        if (adNetwork.length == 0) {
            throw new RuntimeException("At least one ad network must be provided, otherwise this setup does not make any sense.");
        }
        for (int i = 0; i < adNetwork.length; i++) {
            mAdNetworks.add(context.getString(adNetwork[i]));
        }
    }

    public GDPRSetup(String... adNetwork) {
        if (adNetwork.length == 0) {
            throw new RuntimeException("At least one ad network must be provided, otherwise this setup does not make any sense.");
        }
        for (int i = 0; i < adNetwork.length; i++) {
            mAdNetworks.add(adNetwork[i]);
        }
    }

    public GDPRSetup withAllowUsageWithoutConsent(boolean allowUsageWithoutConsent) {
        mAllowUsageWithoutConsent = allowUsageWithoutConsent;
        return this;
    }

    // ----------------
    // Functions
    // ----------------

    public String getNetworksCommaSeperated() {
        String networks = mAdNetworks.get(0);
        for (int i = 1; i < mAdNetworks.size(); i++) {
            networks += ", " + mAdNetworks.get(i);
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
        in.readStringList(mAdNetworks);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mAllowUsageWithoutConsent ? (byte) 1 : 0);
        dest.writeStringList(mAdNetworks);
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
