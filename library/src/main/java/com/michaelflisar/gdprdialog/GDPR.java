package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.michaelflisar.gdprdialog.helper.CheckLocationAsyncTask;

public class GDPR {
    // ------------------
    // Singleton
    // ------------------

    private static GDPR instance;

    private GDPR() {
    }

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

    private GDPRConsentState mCachedConsent = null;

    private CheckLocationAsyncTask mCheckLocationAsyncTask = null;

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
     * <p>
     * it will call the callback activity's::onConsentNeedsToBeRequested() function if the
     * user should be asked for consent, otherwise it will directly call the onConsentInfoUpdate(consent, isNewState) function
     *
     * @param activity the callback activity that implements the callback interface
     * @param setup    the setup
     * @return
     */
    public <T extends AppCompatActivity & IGDPRCallback> void checkIfNeedsToBeShown(T activity, GDPRSetup setup) {
        checkIsInitialised();

        GDPRConsentState consent = getConsent();
        boolean checkConsent = false;
        switch (consent.getConsent()) {
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
                mCheckLocationAsyncTask = new CheckLocationAsyncTask(activity, consent);
                mCheckLocationAsyncTask.execute();
            } else {
                activity.onConsentNeedsToBeRequested(GDPRLocation.UNKNOWN);
            }
        } else {
            // nothing to do, we already know the users decision!
            // simple forward this information to the listener
            activity.onConsentInfoUpdate(consent, false);
        }
    }

    public void cancelRunningTasks() {
        if (mCheckLocationAsyncTask != null) {
            mCheckLocationAsyncTask.cancel(true);
            mCheckLocationAsyncTask = null;
        }
    }

    public GDPRConsentState getConsent() {
        checkIsInitialised();

        if (mCachedConsent == null) {
            int consent = mPreferences.getInt(mContext.getString(R.string.gdpr_preference), 0);
            int location = mPreferences.getInt(mContext.getString(R.string.gdpr_preference_is_in_eea_or_unknown), 0);
            long date = mPreferences.getLong(mContext.getString(R.string.gdpr_preference_date), 0);
            int version = mPreferences.getInt(mContext.getString(R.string.gdpr_preference_app_version), 0);
            mCachedConsent = new GDPRConsentState(GDPRConsent.values()[consent], GDPRLocation.values()[location], date, version);
        }
        return mCachedConsent;
    }

    /**
     * return whether we can use personal informations or not
     *
     * @param alwaysAllowOutsideEAA if true, any user who's consent was requested from outside the EAA will implicitly be handled as a user who has given consent, otherwise we check the explicit user decision
     * @return true, if we can collect personal informations, false otherwise
     */
    public boolean canCollectPersonalInformation(boolean alwaysAllowOutsideEAA) {
        GDPRConsentState consentState = getConsent();
        GDPRConsent consent = consentState.getConsent();

        // if user has given consent for personal data usage, we can collect personal information
        if (consent.equals(GDPRConsent.PERSONAL_CONSENT)) {
            return true;
        }

        // eventually check request location
        if (alwaysAllowOutsideEAA) {
            GDPRLocation location = consentState.getLocation();

            // If we are not in a GDPR region, we can freely collect user data
            if (location.equals(GDPRLocation.NOT_IN_EAA)) {
                return true;
            }
            // in any other case (we don't know the location or request was from putside the EAA), we can not collect personal data
            else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void resetConsent() {
        checkIsInitialised();
        setConsent(new GDPRConsentState());
    }

    public boolean setConsent(GDPRConsentState consentState) {
        mCachedConsent = consentState;
        return mPreferences
                .edit()
                .putInt(mContext.getString(R.string.gdpr_preference), consentState.getConsent().ordinal())
                .putInt(mContext.getString(R.string.gdpr_preference_is_in_eea_or_unknown), consentState.getLocation().ordinal())
                .putLong(mContext.getString(R.string.gdpr_preference_date), consentState.getDate())
                .putInt(mContext.getString(R.string.gdpr_preference_app_version), consentState.getVersion())
                .commit();
    }

    public void showDialog(AppCompatActivity activity, GDPRSetup setup, GDPRLocation location) {
        if (activity.getSupportFragmentManager().findFragmentByTag(GDPRDialog.class.getName()) == null) {
            GDPRDialog dlg = GDPRDialog.newInstance(setup, location);
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

    public interface IGDPRCallback {
        /**
         * Callback to request consent
         * Comes after the flag in settings have been checked and (depednign on the GDPRSetup), the users location has been checked
         *
         * @param location location from where the request is comming from
         */
        void onConsentNeedsToBeRequested(GDPRLocation location);

        /**
         * Callback that will inform about which consent state the user has selected
         *
         * @param consentState the current consent state
         * @param isNewState   flag that indicates if a old consent state was loaded or if this is the new consent state a user have just given
         */
        void onConsentInfoUpdate(GDPRConsentState consentState, boolean isNewState);
    }
}
