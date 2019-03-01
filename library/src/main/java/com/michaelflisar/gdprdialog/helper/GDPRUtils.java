package com.michaelflisar.gdprdialog.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.ColorInt;
import android.telephony.TelephonyManager;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRNetwork;
import com.michaelflisar.gdprdialog.GDPRSubNetwork;
import com.michaelflisar.gdprdialog.R;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

public class GDPRUtils {
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
    public static Boolean isRequestInEAAOrUnknownViaTelephonyManagerCheck(Context context) {
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
            GDPR.getInstance().getLogger().error("GDPRUtils", "Could not get location from telephony manager via SimCountry", e);
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
            GDPR.getInstance().getLogger().error("GDPRUtils", "Could not load location from network via NetworkCountry", e);
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
            GDPR.getInstance().getLogger().error("GDPRUtils", "Could not get location from TimeZone", e);
        }

        return error ? null : false;
    }

    /**
     * checks the location via {@link Locale}
     *
     * @return true, if location is within EAA, false if not and null in case of an error
     */
    public static Boolean isRequestInEAAOrUnknownViaLocaleCheck() {
        boolean error = false;

        /* is eu locale id */
        try {
            Locale locale = Locale.getDefault();
            String localeCountry = locale.getCountry();
            if (EUCountry.contains(localeCountry)) {
                return true;
            }
        } catch (Exception e) {
            error = true;
            GDPR.getInstance().getLogger().error("GDPRUtils", "Could not get location from Locale", e);
        }

        return error ? null : false;
    }

    /**
     * returns a comma seperated string of all items passed in; additioanlly it uses the seperator and last seperator defined in the resources
     *
     * @param context context used to get seperators
     * @param values  a list of values that should be concatenated
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

    public static String getNetworksString(GDPRNetwork[] networks, Context context, boolean withLinks) {
        StringBuilder sb = new StringBuilder("");
        HashSet<String> uniqueNetworks = new HashSet<>();
        for (int i = 0; i < networks.length; i++) {
            boolean addIntermediatorLink = networks[i].getSubNetworks().size() == 0;
            String text = withLinks ? networks[i].getHtmlLink(context, addIntermediatorLink, true) : networks[i].getName();
            if (uniqueNetworks.add(text)) {
                if (sb.length() > 0) {
                    sb.append("<br>");
                }
                sb
                        .append("&#8226;&nbsp;")
                        .append(withLinks ? networks[i].getHtmlLink(context, addIntermediatorLink, false) : networks[i].getName());
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

//    public static int getThemeColor(Context context, int attr) {
//        TypedValue typedValue = new TypedValue();
//        Resources.Theme theme = context.getTheme();
//        theme.resolveAttribute(attr, typedValue, true);
////        @ColorInt int color = typedValue.data;
//        TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{attr});
//        @ColorInt int color = arr.getColor(0, -1);
//        arr.recycle();
//        return color;
//    }

//    public static boolean isColorDark(int color) {
//        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
//        if (darkness < 0.5) {
//            return false; // It's a light color
//        } else {
//            return true; // It's a dark color
//        }
//    }
}
