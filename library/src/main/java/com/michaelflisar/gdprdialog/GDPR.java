package com.michaelflisar.gdprdialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GDPR
{
    // ------------------
    // Singleton
    // ------------------

    private static GDPR instance;
    private GDPR () { }
    public static GDPR getInstance() {
        if (GDPR.instance == null) {
            GDPR.instance = new GDPR();
        }
        return GDPR.instance;
    }

    // ------------------
    // Variables
    // ------------------

    private Context mContext = null;
    private SharedPreferences mPreferences = null;
    private GDPRConsent mCachedValue = null;

    // ------------------
    // GDPR - init
    // ------------------

    public void init(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = context.getSharedPreferences(context.getString(R.string.gdpr_preference_file), Context.MODE_PRIVATE);

        // Init networks
        GDPRDefinitions.init(context);
    }

    // ------------------
    // GDPR - public functions
    // ------------------

    public <T extends AppCompatActivity & IGDPRActivity> void showIfNecessary(T activity, GDPRSetup setup) {
        checkIsInitialised();

        GDPRConsent consent = getConsent();
        switch (consent) {
            case UNKNOWN:
                showDialog(activity, setup);
                break;
            case NO_CONSENT:
                if (!setup.isAllowUsageWithoutConsent()) {
                    showDialog(activity, setup);
                }
            case NON_PERSONAL_CONSENT_ONLY:
            case PERSONAL_CONSENT:
                // nothing to do, we already know the users decision!
                // simple forward this information to the listener
                activity.onConsentInfoUpdate(consent, false);
                break;
        }
    }

    public GDPRConsent getConsent() {
        checkIsInitialised();

        if (mCachedValue == null) {
            int value = mPreferences.getInt(mContext.getString(R.string.gdpr_preference), 0);
            mCachedValue = GDPRConsent.values()[value];
        }
        return mCachedValue;
    }

    public void resetConsent() {
        checkIsInitialised();
        setConsent(GDPRConsent.UNKNOWN);
    }

    public boolean setConsent(GDPRConsent consent) {
        mCachedValue = consent;
        return mPreferences.edit().putInt(mContext.getString(R.string.gdpr_preference), consent.ordinal()).commit();
    }

    // ------------------
    // private helper functions
    // ------------------

    private void checkIsInitialised() {
        if (mPreferences == null) {
            throw new RuntimeException("You have not initialised GDPR. Plase call 'GDPR.getInstance().init(context)' once from anywhere, preferable from your application.");
        }
    }

    private void showDialog(AppCompatActivity activity, GDPRSetup setup) {
        if (activity.getSupportFragmentManager().findFragmentByTag(GDPRDialog.class.getName()) == null) {
            GDPRDialog dlg = GDPRDialog.newInstance(setup);
            dlg.show(activity.getSupportFragmentManager(), GDPRDialog.class.getName());
        }
    }

    // ------------------
    // Callback interfaces
    // ------------------

    public interface IGDPRActivity
    {
        /**
         * Callback that will inform about which consent state the user has selected
         *
         * @param consentState the current consent state
         * @param isNewState flag that indicates if a old consent state was loaded or if this is the new consent state a user have just given
         */
        void onConsentInfoUpdate(GDPRConsent consentState, boolean isNewState);
    }
}
