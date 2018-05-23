package com.michaelflisar.gdprdialog;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.HashSet;

public class GDPRSetup implements Parcelable {

    private String mPolicyLink = null;
    private boolean mHasPaidVersion = false;
    private boolean mAllowNonPersonalisedForPaidVersion = false;
    private boolean mAllowNoConsent = false;
    private GDPRNetwork mAdNetworks[];

    private boolean mExplicitAgeConfirmation = false;
    private boolean mExplicitNonPersonalisedConfirmation = false;
    private boolean mNoToolbarTheme = false;
    private boolean mCheckRequestLocation = false;
    private boolean mUseBottomSheet = false;
    private boolean mForceSelection = false;
    private int mCustomDialogTheme = 0;
    private boolean mShortQuestion = false;

    public GDPRSetup(GDPRNetwork... adNetworks) {
        if (adNetworks == null || adNetworks.length == 0) {
            throw new RuntimeException("At least one ad network must be provided, otherwise this setup does not make any sense.");
        }
        mAdNetworks = adNetworks;
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
     * use thsi if you use an app theme without a toolbar as actionbar
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
        HashSet<String> uniqueNetworks = new HashSet<>();
        for (GDPRNetwork network : mAdNetworks) {
            uniqueNetworks.add(withLinks ? network.getHtmlLink() :network.getName());
        }
        return getCommaSeperatedString(context, uniqueNetworks);
    }

    public final String policyLink() {
        return mPolicyLink;
    }

    public final GDPRNetwork[] networks() {
        return mAdNetworks;
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
        return mAllowNoConsent  || mAllowNonPersonalisedForPaidVersion;
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

    public final boolean forceSelection() {
        return mForceSelection;
    }

    public int customDialogTheme() {
        return mCustomDialogTheme;
    }

    public boolean shortQuestion() {
        return mShortQuestion;
    }

    public final boolean containsAdNetwork() {
        for (GDPRNetwork network : mAdNetworks) {
            if (network.isAdNetwork()) {
                return true;
            }
        }
        return false;
    }

    public HashSet<String> getNetworkTypes() {
        HashSet<String> uniqueTypes = new HashSet<>();
        for (GDPRNetwork network : mAdNetworks) {
            uniqueTypes.add(network.getType());
        }
        return uniqueTypes;
    }

    public String getNetworkTypesCommaSeperated(Context context) {
        return getCommaSeperatedString(context, getNetworkTypes());
    }

    private String getCommaSeperatedString(Context context, Collection<String> values) {
        String innerSep = context.getString(R.string.gdpr_list_seperator);
        String lastSep = context.getString(R.string.gdpr_last_list_seperator);
        String sep;

        String types = "";
        int i = 0;
        for (String value : values) {
            if (i == 0) {
                types = value;
            } else {
                sep = i == values.size() - 1 ? lastSep : innerSep;
                types += sep + value;
            }
            i++;
        }

        return types;
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
        mAdNetworks = new GDPRNetwork[adNetworks.length];
        for (int i = 0; i < adNetworks.length; i++) {
            mAdNetworks[i] = (GDPRNetwork)adNetworks[i];
        }
        mExplicitAgeConfirmation = in.readByte() == 1;
        mExplicitNonPersonalisedConfirmation = in.readByte() == 1;
        mNoToolbarTheme = in.readByte() == 1;
        mCheckRequestLocation = in.readByte() == 1;
        mUseBottomSheet = in.readByte() == 1;
        mForceSelection = in.readByte() == 1;
        mCustomDialogTheme = in.readInt();
        mShortQuestion = in.readByte() == 1;
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
        dest.writeParcelableArray(mAdNetworks, 0);
        dest.writeByte(mExplicitAgeConfirmation ? (byte) 1 : 0);
        dest.writeByte(mExplicitNonPersonalisedConfirmation ? (byte) 1 : 0);
        dest.writeByte(mNoToolbarTheme ? (byte) 1 : 0);
        dest.writeByte(mCheckRequestLocation ? (byte) 1 : 0);
        dest.writeByte(mUseBottomSheet ? (byte) 1 : 0);
        dest.writeByte(mForceSelection ? (byte) 1 : 0);
        dest.writeInt(mCustomDialogTheme);
        dest.writeByte(mShortQuestion ? (byte) 1 : 0);
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
