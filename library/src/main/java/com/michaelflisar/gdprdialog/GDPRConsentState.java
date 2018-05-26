package com.michaelflisar.gdprdialog;

import android.content.Context;

import com.michaelflisar.gdprdialog.helper.GDPRUtils;

import java.util.Date;

public class GDPRConsentState {
    private GDPRConsent mConsent;
    private GDPRLocation mLocation;
    private long mDate;
    private int mVersion;

    public GDPRConsentState() {
        mConsent = GDPRConsent.UNKNOWN;
        mLocation = GDPRLocation.UNDEFINED;
        mDate = -1;
        mVersion = -1;
    }

    public GDPRConsentState(GDPRConsent consent, GDPRLocation location, long date, int version) {
        mConsent = consent;
        mLocation = location;
        mDate = date;
        mVersion = version;
    }

    public GDPRConsentState(Context context, GDPRConsent consent, GDPRLocation location) {
        mConsent = consent;
        mLocation = location;
        mDate = new Date().getTime();
        mVersion = GDPRUtils.getAppVersion(context);
    }

    public final GDPRConsent getConsent() {
        return mConsent;
    }

    public final GDPRLocation getLocation() {
        return mLocation;
    }

    public final long getDate() {
        return mDate;
    }

    public final int getVersion() {
        return mVersion;
    }

    public String logString() {
        return String.format("{ %s [Location: %s | Date: %s | Version: %d]}", mConsent.name(), mLocation.name(), new Date(mDate).toLocaleString(), mVersion);
    }
}