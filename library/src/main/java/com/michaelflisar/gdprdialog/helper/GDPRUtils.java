package com.michaelflisar.gdprdialog.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.michaelflisar.gdprdialog.GDPRNetwork;
import com.michaelflisar.gdprdialog.GDPRSubNetwork;
import com.michaelflisar.gdprdialog.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

public class GDPRUtils
{
    /**
     * get the current app version code
     *
     * @param context any context that is used to get the app verion code
     * @return the app version or -1 if something went wrong
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * checks if the user is currently within the EAA or not
     *
     * @param context
     * @return true, if location is within EAA, false if not and null if something went wrong (timeout, no internet)
     */
    public static Boolean isRequestInEAAOrUnknown(Context context) throws IOException, JSONException {
        JSONObject jsonObject = getJSONAnswerFromGooglesIsInEAAOrUnknownCheck(context);
        return jsonObject != null ? jsonObject.getBoolean(context.getString(R.string.gdpr_googles_check_json_field_is_request_in_eea_or_unknown)) : null;
    }

    /**
     * retrieves the JSON result from google's server which will check if the user is in the EAA or not
     *
     * @param context context
     * @return result from googles server
     */
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

        return new JSONObject(sb.toString());
    }

    private enum EUCountry {
        AT, BE, BG, HR, CY, CZ, DK, EE, FI, FR, DE, GR, HU, IE, IT, LV, LT, LU, MT, NL, PL, PT, RO, SK, SI, ES, SE, GB, //28 member states
        GF, PF, TF, //French territories French Guiana,Polynesia,Southern Territories
        EL, UK,  //alternative EU names for GR and GB
        IS, LI, NO, //not EU but in EAA
        CH, //not in EU or EAA but in single market
        AL, BA, MK, XK, ME, RS, TR; //candidate countries

        public static boolean contains(String s) {
            for (EUCountry eucountry : values())
                if (eucountry.name().equalsIgnoreCase(s))
                    return true;
            return false;
        }
    }

    /**
     * checks the location via {@link TelephonyManager}
     *
     * @param context context used to get {@link TelephonyManager}
     * @return true, if location is within EAA, false if not and null in case of an error
     */
    public static boolean isRequestInEAAOrUnknownViaTelephonyManagerCheck(Context context) {
        boolean error = false;

        /* is eu sim */
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) {
                simCountry = simCountry.toUpperCase();
                if (EUCountry.contains(simCountry)) {
                    return true;
                }
            }
        } catch (Exception e) {
            error = true;
        }


        /* is eu network */
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA && tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) {
                    networkCountry = networkCountry.toUpperCase();
                    if (EUCountry.contains(networkCountry)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            error = true;
        }

        return error ? null : false;
    }

    /**
     * checks the location via {@link TimeZone}
     *
     * @return true, if location is within EAA, false if not and null in case of an error
     */
    public static Boolean isRequestInEAAOrUnknownViaTimezoneCheck() {
        boolean error = false;

        /* is eu time zone id */
        try {
            String tz = TimeZone.getDefault().getID().toLowerCase();
            if (tz.length() < 10) {
                error = true;
            } else if (tz.contains("euro")) {
                return true;
            }
        } catch (Exception e) {
            error = true;
        }

        return error ? null : false;
    }

    /**
     * returns a comma seperated string of all items passed in; additioanlly it uses the seperator and last seperator defined in the resources
     *
     * @param context context used to get seperators
     * @param values a list of values that should be concatenated
     * @return the comma seperated string
     */
    public static String getCommaSeperatedString(Context context, Collection<String> values) {
        String innerSep = context.getString(R.string.gdpr_list_seperator);
        String lastSep = context.getString(R.string.gdpr_last_list_seperator);
        String sep;

        String types = "";
        int i = 0;
        for (String value : values) {
            if (i == 0) {
                types = value;
            } else {
                sep = i == values.size() - 1 ? lastSep : innerSep;
                types += sep + value;
            }
            i++;
        }

        return types;
    }

    public static String getNetworksString(GDPRNetwork[] networks, Context context, boolean withLinks, boolean showAsList) {
        if (!showAsList) {
            HashSet<String> uniqueNetworks = new HashSet<>();
            for (GDPRNetwork network : networks) {
                uniqueNetworks.add(withLinks ? network.getHtmlLink(context, true) : network.getName());
            }
            return GDPRUtils.getCommaSeperatedString(context, uniqueNetworks);
        } else {
            StringBuilder sb = new StringBuilder("");
            HashSet<String> uniqueNetworks = new HashSet<>();
            for (int i = 0; i < networks.length; i++) {
                String text = withLinks ? networks[i].getHtmlLink(context, true) : networks[i].getName();
                if (uniqueNetworks.add(text)) {
                    if (sb.length() > 0) {
                        sb.append("<br>");
                    }
                    sb
                            .append("&#8226;&nbsp;")
                            .append(withLinks ? networks[i].getHtmlLink(context, false) : networks[i].getName());
                    for (GDPRSubNetwork subNetwork : networks[i].getSubNetworks()) {
                        sb
                                .append("<br>")
                                .append("&nbsp;&nbsp;&#9702;&nbsp;")
                                .append(withLinks ? subNetwork.getHtmlLink() : subNetwork.getName());
                    }
                }
            }
            return sb.toString();
        }
    }
}
