package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.michaelflisar.gdprdialog.helper.PreperationAsyncTask;
import com.michaelflisar.gdprdialog.helper.GDPRPreperationData;

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
    private ILogger mLogger = new EmptyLogger();

    private GDPRConsentState mCachedConsent = null;

    private PreperationAsyncTask mPreperationAsyncTask = null;

    // ------------------
    // GDPR - init
    // ------------------

    public GDPR init(Context context) {
        mContext = context.getApplicationContext();
        mPreferences = context.getSharedPreferences(context.getString(R.string.gdpr_preference_file), Context.MODE_PRIVATE);

        // Init networks
        GDPRDefinitions.init(context);

        return this;
    }

    public GDPR initLogger(ILogger logger) {
        mLogger = logger;
        return this;
    }

    // ------------------
    // GDPR - public functions
    // ------------------

    /**
     * Checks if you must require consent from the user
     * <p>
     * it will call the callback {@link IGDPRCallback#onConsentNeedsToBeRequested(GDPRPreperationData)} function if the
     * user should be asked for consent, otherwise it will directly call the {@link IGDPRCallback#onConsentInfoUpdate(GDPRConsentState, boolean)} function
     *
     * @param activity the callback activity that implements the callback interface
     * @param setup    the setup
     */
    public <T extends AppCompatActivity & IGDPRCallback> void checkIfNeedsToBeShown(T activity, GDPRSetup setup) {
        checkIsInitialised();

        GDPRConsentState consent = getConsentState();
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
            case AUTOMATIC_PERSONAL_CONSENT:
                break;
        }

        mLogger.debug("GDPR", String.format("consent check needed: %b, current consent: %s", checkConsent, consent.logString()));

        if (checkConsent) {
            if (setup.needsPreperation()) {
                mPreperationAsyncTask = new PreperationAsyncTask(activity, setup);
                mPreperationAsyncTask.execute();
            } else {
                activity.onConsentNeedsToBeRequested(new GDPRPreperationData().setManuallyUndefined());
            }
        } else {
            // nothing to do, we already know the users decision!
            // simple forward this information to the listener
            activity.onConsentInfoUpdate(consent, false);
        }
    }

    public void cancelRunningTasks() {
        if (mPreperationAsyncTask != null) {
            mPreperationAsyncTask.cancel(true);
            mPreperationAsyncTask = null;
        }
    }

    public GDPRConsentState getConsentState() {
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
     * @return true, if we can collect personal informations, false otherwise
     */
    public boolean canCollectPersonalInformation() {
        // if user has given consent for personal data usage, we can collect personal information
        if (getConsentState().getConsent().isPersonalConsent()) {
            return true;
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

        boolean success = mPreferences
                .edit()
                .putInt(mContext.getString(R.string.gdpr_preference), consentState.getConsent().ordinal())
                .putInt(mContext.getString(R.string.gdpr_preference_is_in_eea_or_unknown), consentState.getLocation().ordinal())
                .putLong(mContext.getString(R.string.gdpr_preference_date), consentState.getDate())
                .putInt(mContext.getString(R.string.gdpr_preference_app_version), consentState.getVersion())
                .commit();

        mLogger.debug("GDPR", String.format("consent saved: %s, success: %b", consentState.logString(), success));

        return success;
    }

    public void showDialog(AppCompatActivity activity, GDPRSetup setup, GDPRLocation location) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm.isStateSaved()) {
            // in this case, activity will be destroyed, we ignore this call
            return;
        }
        if (fm.findFragmentByTag(GDPRDialog.class.getName()) == null) {
            GDPRDialog dlg = GDPRDialog.newInstance(setup, location);
            dlg.show(activity.getSupportFragmentManager(), GDPRDialog.class.getName());
        }
    }

    public ILogger getLogger() {
        return mLogger;
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
    // Interfaces
    // ------------------

    public interface IGDPRCallback {
        /**
         * Callback to request consent
         * Comes after the flag in settings have been checked and (depending on the GDPRSetup) the users location and other data has been checked
         *
         * @param data location and other data
         */
        void onConsentNeedsToBeRequested(GDPRPreperationData data);

        /**
         * Callback that will inform about which consent state the user has selected
         *
         * @param consentState the current consent state
         * @param isNewState   flag that indicates if a old consent state was loaded or if this is the new consent state a user have just given
         */
        void onConsentInfoUpdate(GDPRConsentState consentState, boolean isNewState);
    }

    public interface ILogger {

        void debug(String tag, String info);

        void error(String tag, String msg, Throwable tr);
    }

    // ------------------
    // classes
    // ------------------

    class EmptyLogger implements ILogger {

        @Override
        public void debug(String tag, String info) {

        }

        @Override
        public void error(String tag, String msg, Throwable tr) {

        }
    }
}
