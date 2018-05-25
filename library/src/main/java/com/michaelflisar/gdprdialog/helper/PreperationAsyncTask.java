package com.michaelflisar.gdprdialog.helper;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.michaelflisar.gdprdialog.GDPRSetup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PreperationAsyncTask<T extends AppCompatActivity & GDPR.IGDPRCallback> extends AsyncTask<Object, Void, GDPRPreperationData> {

    private WeakReference<T> mActivity;
    private GDPRSetup mSetup;

    public PreperationAsyncTask(T activity, GDPRSetup setup) {
        mActivity = new WeakReference<>(activity);
        mSetup = setup;
    }

    protected GDPRPreperationData doInBackground(Object... ignored) {
        GDPRPreperationData result = new GDPRPreperationData();

        T activity = mActivity.get();
        if (activity != null) {
            GDPRPreperationData data = new GDPRPreperationData();
            data.load(activity, mSetup.getPublisherIds());
            if (!mSetup.checkRequestLocation()) {
                data.updateLocation(GDPRLocation.UNDEFINED);
            }
            result = data;
        }

        if (mSetup.checkRequestLocation()) {
            // eventually use fallback methods
            if (result.hasError() && mSetup.useLocationCheckTelephonyManagerFallback())
            {
                if (activity != null)
                {
                    result.setManually(GDPRUtils.isRequestInEAAOrUnknownViaTelephonyManagerCheck(activity.getApplicationContext()));
                }
            }
            if (result.hasError() && mSetup.useLocationCheckTimezoneFallback())
            {
                result.setManually(GDPRUtils.isRequestInEAAOrUnknownViaTimezoneCheck());
            }
        }

        return result;
    }

    protected void onPostExecute(GDPRPreperationData result) {
        if (isCancelled()) {
            return;
        }
        T activity = mActivity.get();
        if (activity != null) {
            activity.onConsentNeedsToBeRequested(result);
        }
    }
}
