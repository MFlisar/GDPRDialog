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
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckLocationAsyncTask<T extends AppCompatActivity & GDPR.IGDPRCallback> extends AsyncTask<Object, Void, Boolean> {

    private GDPRConsentState mConsent;
    private T mActivity;

    public CheckLocationAsyncTask(T activity, GDPRConsentState consent) {
        mActivity = activity;
        mConsent = consent;
    }

    protected Boolean doInBackground(Object... ignored) {
        try {
            return GDPRUtils.isRequestInEAAOrUnknown(mActivity);
        } catch (Exception e) {
            return false;
        }
    }

    protected void onPostExecute(Boolean result) {
        if (isCancelled())
            return;
        if (result != null && result) {
            mActivity.onConsentNeedsToBeRequested(result ? GDPRLocation.EAA : GDPRLocation.NOT_IN_EAA);
        } else {
            mActivity.onConsentInfoUpdate(mConsent, false);
        }
    }
}
