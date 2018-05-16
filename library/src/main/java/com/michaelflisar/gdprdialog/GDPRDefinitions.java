package com.michaelflisar.gdprdialog;

import android.content.Context;

public class GDPRDefinitions {

    public static GDPRNetwork ADMOB = null;
    public static GDPRNetwork FIREBASE = null;

    public static void init(Context context) {
        // Init networks
        ADMOB = new GDPRNetwork(context, R.string.gdpr_network_admob, R.string.gdpr_network_admob_link, true, true);
        FIREBASE = new GDPRNetwork(context, R.string.gdpr_network_firebase, R.string.gdpr_network_firebase_link, false, false);
    }
}
