package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.michaelflisar.gdprdialog.helper.CheckLocationAsyncTask;

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

    /**
     * Checks if you must require consent from the user
     *
     * it will call the callback activity's::onConsentNeedsToBeRequested() function if the
     * user should be asked for consent, otherwise it will directly call the onConsentInfoUpdate(consent, isNewState) function
     *
     * @param activity the callback activity that implements the callback interface
     * @param setup the setup
     * @return
     */
    public <T extends AppCompatActivity & IGDPRCallback> void checkIfNeedsToBeShown(T activity, GDPRSetup setup) {
        checkIsInitialised();

        GDPRConsent consent = getConsent();
        boolean checkConsent = false;
        switch (consent) {
            case UNKNOWN:
                checkConsent = true;
                break;
            case NO_CONSENT:
                if (!setup.allowAnyNoConsent()) {
                    checkConsent = true;
                    break;
                }
                break;
            case NON_PERSONAL_CONSENT_ONLY:
            case PERSONAL_CONSENT:
                break;
        }

        if (checkConsent) {
            if (setup.checkRequestLocation()) {
                new CheckLocationAsyncTask(activity, consent).execute();
            } else {
                activity.onConsentNeedsToBeRequested();
            }
        } else {
            // nothing to do, we already know the users decision!
            // simple forward this information to the listener
            activity.onConsentInfoUpdate(consent, false);
        }
    }

//    public <T extends AppCompatActivity & IGDPRCallback> void showIfNecessary(T activity, GDPRSetup setup) {
//        checkIsInitialised();
//
//        if (shouldBeShown(activity, setup)) {
//            showDialog(activity, setup);
//        }
//    }

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

    public void showDialog(AppCompatActivity activity, GDPRSetup setup) {
        if (activity.getSupportFragmentManager().findFragmentByTag(GDPRDialog.class.getName()) == null) {
            GDPRDialog dlg = GDPRDialog.newInstance(setup);
            dlg.show(activity.getSupportFragmentManager(), GDPRDialog.class.getName());
        }
    }

    // ------------------
    // private helper functions
    // ------------------

    private void checkIsInitialised() {
        if (mPreferences == null) {
            throw new RuntimeException("You have not initialised GDPR. Plase call 'GDPR.getInstance().init(context)' once from anywhere, preferable from your application.");
        }
    }

    // ------------------
    // Callback interfaces
    // ------------------

    public interface IGDPRCallback
    {
        /**
         * Callback to request consent
         * Comes after the flag in settings have been checked and (depednign on the GDPRSetup), the users location has been checked
         */
        void onConsentNeedsToBeRequested();

        /**
         * Callback that will inform about which consent state the user has selected
         *
         * @param consentState the current consent state
         * @param isNewState flag that indicates if a old consent state was loaded or if this is the new consent state a user have just given
         */
        void onConsentInfoUpdate(GDPRConsent consentState, boolean isNewState);
    }
}
