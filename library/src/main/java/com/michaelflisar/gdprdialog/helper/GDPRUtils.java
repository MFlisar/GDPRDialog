package com.michaelflisar.gdprdialog.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.michaelflisar.gdprdialog.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GDPRUtils
{
    public static int getAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isRequestInEAAOrUnknown(Context context) throws IOException, JSONException {
        JSONObject jsonObject = getJSONAnswerFromGooglesIsInEAAOrUnknownCheck(context);
        return jsonObject != null && jsonObject.getBoolean(context.getString(R.string.gdpr_googles_check_json_field_is_request_in_eea_or_unknown));
    }

    public static JSONObject getJSONAnswerFromGooglesIsInEAAOrUnknownCheck(Context context) throws IOException, JSONException {
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
