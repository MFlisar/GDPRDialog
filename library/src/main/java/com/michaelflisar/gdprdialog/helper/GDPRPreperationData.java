package com.michaelflisar.gdprdialog.helper;

import android.content.Context;
import android.text.TextUtils;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.michaelflisar.gdprdialog.GDPRSubNetwork;
import com.michaelflisar.gdprdialog.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GDPRPreperationData {
    private GDPRLocation mLocation = GDPRLocation.UNDEFINED;
    private List<GDPRSubNetwork> mSubNetworks = new ArrayList<>();
    private boolean mManualSet = false;
    private boolean mError = false;

    public GDPRPreperationData() {
    }

    public GDPRLocation getLocation() {
        return mLocation;
    }

    public List<GDPRSubNetwork> getSubNetworks() {
        return mSubNetworks;
    }

    public boolean isManualSet() {
        return mManualSet;
    }

    public boolean hasError() {
        return mError;
    }

    public void load(Context context, ArrayList<String> publisherIds, int readTimeout, int connectTimeout) {
        reset();
        JSONObject jsonObject = null;
        try {
            jsonObject = loadJSON(context, publisherIds, readTimeout, connectTimeout);
            if (jsonObject != null) {
                String fieldIsRequestInEaaOrUnknown = context.getString(R.string.gdpr_googles_check_json_field_is_request_in_eea_or_unknown);
                String fieldCompanies = context.getString(R.string.gdpr_googles_check_json_field_companies);
                boolean isInEAAOrUnknown = jsonObject.getBoolean(fieldIsRequestInEaaOrUnknown);
                mLocation = isInEAAOrUnknown ? GDPRLocation.IN_EAA_OR_UNKNOWN : GDPRLocation.NOT_IN_EAA;
                if (jsonObject.has(fieldCompanies)) {
                    String fieldCompanyName = context.getString(R.string.gdpr_googles_check_json_field_company_name);
                    String fieldPolicyUrl = context.getString(R.string.gdpr_googles_check_json_field_policy_url);
                    JSONArray array = jsonObject.getJSONArray(fieldCompanies);
                    for (int i = 0; i < array.length(); i++) {
                        mSubNetworks.add(new GDPRSubNetwork(
                                array.getJSONObject(i).getString(fieldCompanyName),
                                array.getJSONObject(i).getString(fieldPolicyUrl)
                        ));
                    }
                }
            }
        } catch (Exception e) {
            reset();
            mError = true;
            GDPR.getInstance().getLogger().error("GDPRPreperationData::load", "Could not load location from network", e);
        }
    }

    public void updateLocation(GDPRLocation location) {
        mLocation = location;
    }

    public GDPRPreperationData setManually(Boolean isInEAAOrUnknown) {
        reset();
        mManualSet = true;
        if (isInEAAOrUnknown != null) {
            mLocation = isInEAAOrUnknown ? GDPRLocation.IN_EAA_OR_UNKNOWN : GDPRLocation.NOT_IN_EAA;
        } else {
            mError = true;
        }
        return this;
    }

    public GDPRPreperationData setManuallyUndefined() {
        reset();
        mManualSet = true;
        mLocation = GDPRLocation.UNDEFINED;
        return this;
    }

    private void reset() {
        mManualSet = false;
        mLocation = GDPRLocation.UNDEFINED;
        mSubNetworks.clear();
        mError = false;
    }

    private JSONObject loadJSON(Context context, ArrayList<String> publisherIds, int readTimeout, int connectTimeout) throws IOException, JSONException {
        String publisherIdsString = TextUtils.join(",", publisherIds);

        HttpURLConnection urlConnection = null;
        URL url = new URL(context.getString(R.string.gdpr_googles_check_is_eaa_request_url, publisherIdsString));
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(readTimeout );
        urlConnection.setConnectTimeout(connectTimeout);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        return new JSONObject(sb.toString());
    }

    public String logString() {
        return String.format("{ %s - SubNetworks: %d | Error: %b | ManualSet: %b }", mLocation.name(), mSubNetworks.size(), mError, mManualSet);
    }
}
