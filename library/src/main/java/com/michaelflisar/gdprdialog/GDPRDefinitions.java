package com.michaelflisar.gdprdialog;

import android.content.Context;

public class GDPRDefinitions {

    public static GDPRNetwork ADMOB = null;
    public static GDPRNetwork FIREBASE_DATABASE = null;
    public static GDPRNetwork FIREBASE_CRASH = null;

    public static void init(Context context) {
        // Init networks
        ADMOB = new GDPRNetwork(context, R.string.gdpr_network_admob, R.string.gdpr_network_admob_link, R.string.gdpr_type_ads, true, true);
        FIREBASE_DATABASE = new GDPRNetwork(context, R.string.gdpr_network_firebase, R.string.gdpr_network_firebase_link, R.string.gdpr_type_cloud_database, false, false);
        FIREBASE_CRASH = new GDPRNetwork(context, R.string.gdpr_network_firebase, R.string.gdpr_network_firebase_link, R.string.gdpr_type_crash, false, false);
    }
}
