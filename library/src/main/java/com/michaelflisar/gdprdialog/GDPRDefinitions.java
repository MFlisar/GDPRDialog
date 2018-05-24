package com.michaelflisar.gdprdialog;

import android.content.Context;

public class GDPRDefinitions {

    public static GDPRNetwork ADMOB = null;
    public static GDPRNetwork AERSERV = null;
    public static GDPRNetwork INMOBI = null;
    public static GDPRNetwork MOPUB = null;
    public static GDPRNetwork VUNGLE = null;
    public static GDPRNetwork ADCOLONY = null;
    public static GDPRNetwork UNITY = null;
    public static GDPRNetwork APPLOVIN = null;

    public static GDPRNetwork FIREBASE_DATABASE = null;
    public static GDPRNetwork FIREBASE_CRASH = null;
    public static GDPRNetwork FIREBASE_ANALYTICS = null;

    public static void init(Context context) {
        // Init networks
        ADMOB = new GDPRNetwork(context, R.string.gdpr_network_admob, R.string.gdpr_network_admob_link, R.string.gdpr_type_ads, true, true);
        AERSERV = new GDPRNetwork(context, R.string.gdpr_network_aerserv, R.string.gdpr_network_aerserv_link, R.string.gdpr_type_ads, true, true);
        INMOBI = new GDPRNetwork(context, R.string.gdpr_network_inmobi, R.string.gdpr_network_inmobi_link, R.string.gdpr_type_ads, true, true);
        MOPUB = new GDPRNetwork(context, R.string.gdpr_network_mopub, R.string.gdpr_network_mopub_link, R.string.gdpr_type_ads, true, true);
        VUNGLE = new GDPRNetwork(context, R.string.gdpr_network_vungle, R.string.gdpr_network_vungle_link, R.string.gdpr_type_ads, true, true);
        ADCOLONY = new GDPRNetwork(context, R.string.gdpr_network_adcolony, R.string.gdpr_network_adcolony_link, R.string.gdpr_type_ads, true, true);
        UNITY = new GDPRNetwork(context, R.string.gdpr_network_unity, R.string.gdpr_network_unity_link, R.string.gdpr_type_ads, true, true);
        APPLOVIN = new GDPRNetwork(context, R.string.gdpr_network_applovin, R.string.gdpr_network_applovin_link, R.string.gdpr_type_ads, true, true);


        FIREBASE_DATABASE = new GDPRNetwork(context, R.string.gdpr_network_firebase, R.string.gdpr_network_firebase_link, R.string.gdpr_type_cloud_database, false, false);
        FIREBASE_CRASH = new GDPRNetwork(context, R.string.gdpr_network_firebase, R.string.gdpr_network_firebase_link, R.string.gdpr_type_crash, false, false);
        FIREBASE_ANALYTICS = new GDPRNetwork(context, R.string.gdpr_network_firebase, R.string.gdpr_network_firebase_link, R.string.gdpr_type_analytics, false, false);
    }
}
