package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.michaelflisar.gdprdialog.helper.GDPRUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class GDPRSetup implements Parcelable {

    private String mPolicyLink = null;
    private boolean mHasPaidVersion = false;
    private boolean mAllowNonPersonalisedForPaidVersion = false;
    private boolean mAllowNoConsent = false;
    private GDPRNetwork mNetworks[];
    private boolean mExplicitAgeConfirmation = false;
    private boolean mExplicitNonPersonalisedConfirmation = false;
    private boolean mNoToolbarTheme = false;
    private boolean mCheckRequestLocation = false;
    private boolean mUseLocationCheckTelephonyManagerFallback = false;
    private boolean mUseLocationCheckTimezoneFallback = false;
    private boolean mUseBottomSheet = false;
    private boolean mForceSelection = false;
    private int mCustomDialogTheme = 0;
    private boolean mShortQuestion = false;
    private ArrayList<String> mPublisherIds = new ArrayList<>();

    public GDPRSetup(GDPRNetwork... networks) {
        if (networks == null || networks.length == 0) {
            throw new RuntimeException("At least one ad network must be provided, otherwise this setup does not make any sense.");
        }
        mNetworks = networks;
    }

    /**
     * set a custom policy link
     *
     * @param policyLink your custom policy link
     * @return this
     */
    public GDPRSetup withPrivacyPolicy(String policyLink) {
        if (!policyLink.startsWith("https://") && !policyLink.startsWith("http://")) {
            policyLink = "http://" + policyLink;
        }
        mPolicyLink = policyLink;
        return this;
    }

    /**
     * set a custom policy link
     *
     * @param policyLink your custom policy link resource id
     * @return this
     */
    public GDPRSetup withPrivacyPolicy(Context context, int policyLink) {
        return withPrivacyPolicy(context.getString(policyLink));
    }

    /**
     * use this if you offer a paid version
     *
     * @param alsoProvideNonPersonalisedOption true, if the user can select between personalised, non personalised and paid app, false if he cannot select non persnalised ads
     * @return this
     */
    public GDPRSetup withPaidVersion(boolean alsoProvideNonPersonalisedOption) {
        mHasPaidVersion = true;
        mAllowNonPersonalisedForPaidVersion = alsoProvideNonPersonalisedOption;
        return this;
    }

    /**
     * use this to allow the user to also use your app without consent
     * don't show ads in this case
     *
     * @param allowNoConsent true to allow the app usage without any consent, false otherwise
     * @return this
     */
    public GDPRSetup withAllowNoConsent(boolean allowNoConsent) {
        mAllowNoConsent = allowNoConsent;
        return this;
    }

    /**
     * use this to force the user to explicitly confirm his age with a checkbox
     *
     * @param explicitAgeConfirmation true, to force explicit age confirmation, false otherwise
     * @return this
     */
    public GDPRSetup withExplicitAgeConfirmation(boolean explicitAgeConfirmation) {
        mExplicitAgeConfirmation = explicitAgeConfirmation;
        return this;
    }

    /**
     * use this for ads only!
     * use this to force the user to give explicit consent for non personalised data usage
     * the user will see an information about ads and how they work
     *
     * @param explicitNonPersonalisedConfirmation true, to force explicit consent, false otherwise
     * @return this
     */
    public GDPRSetup withExplicitNonPersonalisedConfirmation(boolean explicitNonPersonalisedConfirmation) {
        mExplicitNonPersonalisedConfirmation = explicitNonPersonalisedConfirmation;
        return this;
    }

    /**
     * use this if you use an app theme without a toolbar as actionbar
     *
     * @param noToolbarTheme true, if you use a theme without a toolbar, false otherwise
     * @return this
     */
    public GDPRSetup withNoToolbarTheme(boolean noToolbarTheme) {
        mNoToolbarTheme = noToolbarTheme;
        return this;
    }

    /**
     * use this to check the user's location and check if it is within the EAA before requesting consent
     * this uses a homepage form google and parses it's result
     *
     * @param checkRequestLocation true to check location, false otherwise
     * @return this
     */
    public GDPRSetup withCheckRequestLocation(boolean checkRequestLocation) {
        mCheckRequestLocation = checkRequestLocation;
        return this;
    }

    /**
     * use this to check the user's location and check if it is within the EAA before requesting consent
     * this uses a homepage form google and parses it's result
     *
     * @param checkRequestLocation                     true to check location, false otherwise
     * @param useLocationCheckTelephonyManagerFallback true to check location via the {@link android.telephony.TelephonyManager} if main check fails, false otherwise
     * @param useLocationCheckTimezoneFallback         true to check location via the {@link java.util.TimeZone} if main check fails, false otherwise
     * @return this
     */
    public GDPRSetup withCheckRequestLocation(boolean checkRequestLocation, boolean useLocationCheckTelephonyManagerFallback, boolean useLocationCheckTimezoneFallback) {
        mCheckRequestLocation = checkRequestLocation;
        mUseLocationCheckTelephonyManagerFallback = useLocationCheckTelephonyManagerFallback;
        mUseLocationCheckTimezoneFallback = useLocationCheckTimezoneFallback;
        return this;
    }

    /**
     * ad some publisher ids to load your all your active AdMob networks from google
     *
     * @param publisherIds your publisher id(s)
     * @return this
     */
    public GDPRSetup withLoadAdMobNetworks(String... publisherIds) {
        mPublisherIds.clear();
        for (int i = 0; i < publisherIds.length; i++) {
            mPublisherIds.add(publisherIds[i]);
        }
        return this;
    }

    /**
     * use this to show the dialog as a bottom sheet
     *
     * @param useBottomSheet true to use the bottom sheet style, false otherwise
     * @return this
     */
    public GDPRSetup withBottomSheet(boolean useBottomSheet) {
        mUseBottomSheet = useBottomSheet;
        return this;
    }

    /**
     * use this to disable closing the dialog (and app) with the back button
     *
     * @param forceSelection true to force user to select an option, false otherwise
     * @return this
     */
    public GDPRSetup withForceSelection(boolean forceSelection) {
        mForceSelection = forceSelection;
        return this;
    }

    /**
     * use this to provide a custom dialog theme
     *
     * @param theme the dialog theme to use
     * @return this
     */
    public GDPRSetup withCustomDialogTheme(int theme) {
        mCustomDialogTheme = theme;
        return this;
    }

    /**
     * use this to not explicitly show a text about personalised ads to keep the dialog a little smaller
     * the info will still state that you use personalised data for ads, but not explicitly in the question
     *
     * @param shortQuestion true to show a short question only, false otherwise
     * @return this
     */
    public GDPRSetup withShortQuestion(boolean shortQuestion) {
        mShortQuestion = shortQuestion;
        return this;
    }

    // ----------------
    // Functions
    // ----------------

    public final String getNetworksCommaSeperated(Context context, boolean withLinks) {
        return GDPRUtils.getNetworksString(mNetworks, context, withLinks);
    }

    public final String policyLink() {
        return mPolicyLink;
    }

    public final GDPRNetwork[] networks() {
        return mNetworks;
    }

    public final boolean hasPaidVersion() {
        return mHasPaidVersion;
    }

    public final boolean allowNonPersonalisedForPaidVersion() {
        return mAllowNonPersonalisedForPaidVersion;
    }

    public final boolean allowNoConsent() {
        return mAllowNoConsent;
    }

    public final boolean allowAnyNoConsent() {
        return mAllowNoConsent || mAllowNonPersonalisedForPaidVersion;
    }

    public final boolean explicitAgeConfirmation() {
        return mExplicitAgeConfirmation;
    }

    public final boolean explicitNonPersonalisedConfirmation() {
        return mExplicitNonPersonalisedConfirmation;
    }

    public final boolean noToolbarTheme() {
        return mNoToolbarTheme;
    }

    public final boolean useBottomSheet() {
        return mUseBottomSheet;
    }

    public final boolean checkRequestLocation() {
        return mCheckRequestLocation;
    }

    public final boolean needsPreperation() {
        return mCheckRequestLocation || mPublisherIds.size() > 0;
    }

    public final ArrayList<String> getPublisherIds() {
        return mPublisherIds;
    }

    public final boolean forceSelection() {
        return mForceSelection;
    }

    public int customDialogTheme() {
        return mCustomDialogTheme;
    }

    public boolean shortQuestion() {
        return mShortQuestion;
    }

    public boolean useLocationCheckTelephonyManagerFallback() {
        return mUseLocationCheckTelephonyManagerFallback;
    }

    public boolean useLocationCheckTimezoneFallback() {
        return mUseLocationCheckTimezoneFallback;
    }

    public final boolean containsAdNetwork() {
        for (GDPRNetwork network : mNetworks) {
            if (network.isAdNetwork()) {
                return true;
            }
        }
        return false;
    }

    public HashSet<String> getNetworkTypes() {
        HashSet<String> uniqueTypes = new HashSet<>();
        for (GDPRNetwork network : mNetworks) {
            uniqueTypes.add(network.getType());
        }
        return uniqueTypes;
    }

    public String getNetworkTypesCommaSeperated(Context context) {
        return GDPRUtils.getCommaSeperatedString(context, getNetworkTypes());
    }

    // ----------------
    // Parcelable
    // ----------------

    public GDPRSetup(Parcel in) {
        mPolicyLink = in.readString();
        mHasPaidVersion = in.readByte() == 1;
        mAllowNonPersonalisedForPaidVersion = in.readByte() == 1;
        mAllowNoConsent = in.readByte() == 1;
        Parcelable[] adNetworks = in.readParcelableArray(GDPRNetwork.class.getClassLoader());
        mNetworks = new GDPRNetwork[adNetworks.length];
        for (int i = 0; i < adNetworks.length; i++) {
            mNetworks[i] = (GDPRNetwork) adNetworks[i];
        }
        mExplicitAgeConfirmation = in.readByte() == 1;
        mExplicitNonPersonalisedConfirmation = in.readByte() == 1;
        mNoToolbarTheme = in.readByte() == 1;
        mCheckRequestLocation = in.readByte() == 1;
        mUseBottomSheet = in.readByte() == 1;
        mForceSelection = in.readByte() == 1;
        mCustomDialogTheme = in.readInt();
        mShortQuestion = in.readByte() == 1;
        mUseLocationCheckTelephonyManagerFallback = in.readByte() == 1;
        mUseLocationCheckTimezoneFallback = in.readByte() == 1;
        in.readStringList(mPublisherIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPolicyLink);
        dest.writeInt(mHasPaidVersion ? (byte) 1 : 0);
        dest.writeInt(mAllowNonPersonalisedForPaidVersion ? (byte) 1 : 0);
        dest.writeInt(mAllowNoConsent ? (byte) 1 : 0);
        dest.writeParcelableArray(mNetworks, 0);
        dest.writeByte(mExplicitAgeConfirmation ? (byte) 1 : 0);
        dest.writeByte(mExplicitNonPersonalisedConfirmation ? (byte) 1 : 0);
        dest.writeByte(mNoToolbarTheme ? (byte) 1 : 0);
        dest.writeByte(mCheckRequestLocation ? (byte) 1 : 0);
        dest.writeByte(mUseBottomSheet ? (byte) 1 : 0);
        dest.writeByte(mForceSelection ? (byte) 1 : 0);
        dest.writeInt(mCustomDialogTheme);
        dest.writeByte(mShortQuestion ? (byte) 1 : 0);
        dest.writeByte(mUseLocationCheckTelephonyManagerFallback ? (byte) 1 : 0);
        dest.writeByte(mUseLocationCheckTimezoneFallback ? (byte) 1 : 0);
        dest.writeStringList(mPublisherIds);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GDPRSetup createFromParcel(Parcel in) {
            return new GDPRSetup(in);
        }

        public GDPRSetup[] newArray(int size) {
            return new GDPRSetup[size];
        }
    };
}
