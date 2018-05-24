package com.michaelflisar.gdprdialog.helper;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsentState;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.michaelflisar.gdprdialog.GDPRSetup;

import java.lang.ref.WeakReference;

public class CheckLocationAsyncTask<T extends AppCompatActivity & GDPR.IGDPRCallback> extends AsyncTask<Object, Void, Boolean> {

    private WeakReference<T> mActivity;
    private GDPRSetup mSetup;

    public CheckLocationAsyncTask(T activity, GDPRSetup setup) {
        mActivity = new WeakReference<>(activity);
        mSetup = setup;
    }

    protected Boolean doInBackground(Object... ignored) {
        try {
            T activity = mActivity.get();
            if (activity != null) {
                Boolean isInEAAOrUnkown = GDPRUtils.isRequestInEAAOrUnknown(activity.getApplicationContext());
                // eventually use fallback methods
                if (isInEAAOrUnkown == null && mSetup.useLocationCheckTelephonyManagerFallback()) {
                    isInEAAOrUnkown = GDPRUtils.isRequestInEAAOrUnknownViaTelephonyManagerCheck(activity.getApplicationContext());
                }
                if (isInEAAOrUnkown == null && mSetup.useLocationCheckTimezoneFallback()) {
                    isInEAAOrUnkown = GDPRUtils.isRequestInEAAOrUnknownViaTimezoneCheck();
                }
                return isInEAAOrUnkown;
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Boolean result) {
        if (isCancelled()) {
            return;
        }
        T activity = mActivity.get();
        if (activity == null) {
            return;
        }
        if (result != null) {
            activity.onConsentNeedsToBeRequested(result ? GDPRLocation.IN_EAA_OR_UNKNOWN : GDPRLocation.NOT_IN_EAA);
        } else {
            activity.onConsentNeedsToBeRequested(GDPRLocation.IN_EAA_OR_UNKNOWN);
        }
    }
}
