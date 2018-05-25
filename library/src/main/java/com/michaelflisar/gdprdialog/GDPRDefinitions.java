package com.michaelflisar.gdprdialog;

import android.content.Context;

public class GDPRDefinitions {

    // -------------------
    // Ad Networks
    // -------------------

    public static GDPRNetwork ADMOB = null;
    public static GDPRNetwork AERSERV = null;
    public static GDPRNetwork INMOBI = null;
    public static GDPRNetwork MOPUB = null;
    public static GDPRNetwork VUNGLE = null;
    public static GDPRNetwork ADCOLONY = null;
    public static GDPRNetwork UNITY = null;
    public static GDPRNetwork APPLOVIN = null;

    // -------------------
    // Others
    // -------------------

    public static GDPRNetwork FIREBASE_DATABASE = null;
    public static GDPRNetwork FIREBASE_CRASH = null;
    public static GDPRNetwork FIREBASE_ANALYTICS = null;

    public static void init(Context context) {
        // init ad networks
        ADMOB = new GDPRNetwork(context, "AdMob", "https://policies.google.com/privacy", R.string.gdpr_type_ads, true)
                .withIsIntermediator("https://support.google.com/admob/answer/9012903");
        AERSERV = new GDPRNetwork(context, "AerServ", "https://www.aerserv.com/privacy-policy", R.string.gdpr_type_ads, true);
        INMOBI = new GDPRNetwork(context, "InMobi", "https://www.inmobi.com/privacy-policy-for-eea", R.string.gdpr_type_ads, true);
        MOPUB = new GDPRNetwork(context, "MoPub", "https://www.mopub.com/legal/privacy", R.string.gdpr_type_ads, true)
                .withIsIntermediator("https://developers.mopub.com/docs/mediation/supported-mediation-partners");
        VUNGLE = new GDPRNetwork(context, "InMobi", "https://vungle.com/privacy", R.string.gdpr_type_ads, true);
        ADCOLONY = new GDPRNetwork(context, "AdColony", "https://www.adcolony.com/privacy-policy", R.string.gdpr_type_ads, true);
        UNITY = new GDPRNetwork(context, "Unity", "https://unity3d.com/legal/privacy-policy", R.string.gdpr_type_ads, true);
        APPLOVIN = new GDPRNetwork(context, "AppLovin", "https://www.applovin.com/privacy", R.string.gdpr_type_ads, true);

        // init othes
        String firebase = "Firebase";
        String firebaseUrl = "https://firebase.google.com/support/privacy";
        FIREBASE_DATABASE = new GDPRNetwork(context, firebase, firebaseUrl, R.string.gdpr_type_cloud_database, false);
        FIREBASE_CRASH = new GDPRNetwork(context, firebase, firebaseUrl, R.string.gdpr_type_crash, false);
        FIREBASE_ANALYTICS = new GDPRNetwork(context, firebase, firebaseUrl, R.string.gdpr_type_analytics, false);
    }
}
