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
    public static GDPRNetwork FAN = null;
    public static GDPRNetwork APPNEXT = null;
    public static GDPRNetwork MOBVISTA = null;
    public static GDPRNetwork FLURRY_ADS = null;
    public static GDPRNetwork GLISPA = null;
    public static GDPRNetwork TAPJOY = null;
    public static GDPRNetwork APPBRAIN = null;
    public static GDPRNetwork STARTAPP = null;
    public static GDPRNetwork IRONSOURCE = null;

    // -------------------
    // Others
    // -------------------

    public static GDPRNetwork FIREBASE_DATABASE = null;
    public static GDPRNetwork FIREBASE_CRASH = null;
    public static GDPRNetwork FIREBASE_ANALYTICS = null;
    public static GDPRNetwork FLURRY_CRASH = null;
    public static GDPRNetwork FLURRY_ANALYTICS = null;

    public static void init(Context context) {
        // init ad networks
        ADMOB = new GDPRNetwork(context, "AdMob", "https://policies.google.com/privacy", R.string.gdpr_type_ads, true)
                .withIsIntermediator("https://support.google.com/admob/answer/9012903");
        AERSERV = new GDPRNetwork(context, "AerServ", "https://www.aerserv.com/privacy-policy", R.string.gdpr_type_ads, true);
        INMOBI = new GDPRNetwork(context, "InMobi", "https://www.inmobi.com/privacy-policy-for-eea", R.string.gdpr_type_ads, true);
        MOPUB = new GDPRNetwork(context, "MoPub", "https://www.mopub.com/legal/privacy", R.string.gdpr_type_ads, true)
                .withIsIntermediator("https://www.mopub.com/legal/partners/");
        VUNGLE = new GDPRNetwork(context, "InMobi", "https://vungle.com/privacy", R.string.gdpr_type_ads, true);
        ADCOLONY = new GDPRNetwork(context, "AdColony", "https://www.adcolony.com/privacy-policy", R.string.gdpr_type_ads, true);
        UNITY = new GDPRNetwork(context, "Unity", "https://unity3d.com/legal/privacy-policy", R.string.gdpr_type_ads, true);
        APPLOVIN = new GDPRNetwork(context, "AppLovin", "https://www.applovin.com/privacy", R.string.gdpr_type_ads, true);
        FAN = new GDPRNetwork(context, "Facebook", "https://www.facebook.com/privacy/explanation", R.string.gdpr_type_ads, true);
        APPNEXT = new GDPRNetwork(context, "AppNext", "https://www.appnext.com/policy.html#", R.string.gdpr_type_ads, true);
        MOBVISTA = new GDPRNetwork(context, "MobVista", "https://www.mobvista.com/en/privacy/", R.string.gdpr_type_ads, true);
        FLURRY_ADS = new GDPRNetwork(context, "Flurry Ads", "https://policies.oath.com/us/en/oath/privacy/index.html", R.string.gdpr_type_ads, true);
        GLISPA = new GDPRNetwork(context, "Glispa", "https://www.glispa.com/privacy-policy/", R.string.gdpr_type_ads, true);
        TAPJOY = new GDPRNetwork(context, "Tapjoy", "https://dev.tapjoy.com/faq/tapjoy-privacy-policy/", R.string.gdpr_type_ads, true);
        APPBRAIN = new GDPRNetwork(context, "AppBrain", "https://www.appbrain.com/info/help/privacy/index.html", R.string.gdpr_type_ads, true);
        STARTAPP = new GDPRNetwork(context, "StartApp", "https://www.startapp.com/policy/privacy-policy/", R.string.gdpr_type_ads, true);
        IRONSOURCE = new GDPRNetwork(context, "ironSource", "https://developers.ironsrc.com/ironsource-mobile/air/ironsource-mobile-privacy-policy/", R.string.gdpr_type_ads, true);

        // init others
        String firebase = "Firebase";
        String firebaseUrl = "https://firebase.google.com/support/privacy";
        FIREBASE_DATABASE = new GDPRNetwork(context, firebase, firebaseUrl, R.string.gdpr_type_cloud_database, false);
        FIREBASE_CRASH = new GDPRNetwork(context, firebase, firebaseUrl, R.string.gdpr_type_crash, false);
        FIREBASE_ANALYTICS = new GDPRNetwork(context, firebase, firebaseUrl, R.string.gdpr_type_analytics, false);
        FLURRY_ANALYTICS = new GDPRNetwork(context, "Flurry Analytics", "https://policies.oath.com/us/en/oath/privacy/index.html", R.string.gdpr_type_analytics, false);
        FLURRY_CRASH = new GDPRNetwork(context, "Flurry Crash", "https://policies.oath.com/us/en/oath/privacy/index.html", R.string.gdpr_type_crash, false);
    }
}
