package com.michaelflisar.gdprdialog.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
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

    private GDPRConsent mConsent;
    private T mActivity;

    public CheckLocationAsyncTask(T activity, GDPRConsent consent) {
        mActivity = activity;
        mConsent = consent;
    }

    protected Boolean doInBackground(Object... ignored) {
        try {
            JSONObject jsonObject = getJSONAnswerFromGooglesIsInEAAOrUnknownCheck(mActivity);
            return jsonObject != null && jsonObject.getBoolean(mActivity.getString(R.string.gdpr_googles_check_json_field_is_request_in_eea_or_unknown));
        } catch (Exception e) {
            return false;
        }
    }

    protected void onPostExecute(Boolean result) {
        if (result != null && result) {
            mActivity.onConsentNeedsToBeRequested(result ? GDPRLocation.EAA : GDPRLocation.NOT_IN_EAA);
        } else {
            mActivity.onConsentInfoUpdate(mConsent, false);
        }
    }

    private static JSONObject getJSONAnswerFromGooglesIsInEAAOrUnknownCheck(Context context) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(context.getString(R.string.gdpr_googles_check_is_eaa_request_url));
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }
}
