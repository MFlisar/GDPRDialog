package com.michaelflisar.gdprdialog.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRConsentState;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.michaelflisar.gdprdialog.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckLocationAsyncTask<T extends AppCompatActivity & GDPR.IGDPRCallback> extends AsyncTask<Object, Void, Boolean> {

    private GDPRConsentState mConsent;
    private WeakReference<T> mActivity;

    public CheckLocationAsyncTask(T activity, GDPRConsentState consent) {
        mActivity = new WeakReference<>(activity);
        mConsent = consent;
    }

    protected Boolean doInBackground(Object... ignored) {
        try {
            T activity = mActivity.get();
            if (activity != null) {
                return GDPRUtils.isRequestInEAAOrUnknown(activity.getApplicationContext());
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
            activity.onConsentNeedsToBeRequested(result ? GDPRLocation.EAA : GDPRLocation.NOT_IN_EAA);
        } else {
            activity.onConsentInfoUpdate(mConsent, false);
        }
    }
}
