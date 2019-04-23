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
    private GDPRLocationCheck mRequestLocationChecks[];
    private boolean mUseBottomSheet = false;
    private boolean mForceSelection = false;
    private int mCustomDialogTheme = 0;
    private boolean mShortQuestion = false;
    private ArrayList<String> mPublisherIds = new ArrayList<>();
    private boolean mShowPaidOrFreeInfoText = true;
    private GDPRCustomTexts mCustomTexts = new GDPRCustomTexts();

    private int mConnectionReadTimeout = 3000;
    private int mConnectionConnectTimeout = 5000;

    public GDPRSetup(GDPRNetwork... networks) {
        if (networks == null || networks.length == 0) {
            throw new RuntimeException("At least one ad network must be provided, otherwise this setup does not make any sense.");
        }
        mNetworks = networks;
        mRequestLocationChecks = new GDPRLocationCheck[0];
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
     * @param locationChecks the {@link GDPRLocationCheck} that should be used to check locations ordered by their priority - use {@link GDPRLocationCheck#INTERNET} for the default online location check
     * @return this
     */
    public GDPRSetup withCheckRequestLocation(GDPRLocationCheck... locationChecks) {
        if (locationChecks == null) {
            locationChecks = new GDPRLocationCheck[0];
        }
        mRequestLocationChecks = locationChecks;
        return this;
    }

    /**
     * sets the connection timeouts for retrieving the location
     *
     * @param readTimeout timeout for reading
     * @param connectTimeout timeout for connection
     * @return this
     */
    public GDPRSetup withCheckRequestLocationTimeouts(int readTimeout, int connectTimeout) {
        mConnectionReadTimeout = readTimeout;
        mConnectionConnectTimeout = connectTimeout;
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

    /**
     * use this to remove the info in the main dialog that the app is kept free / cheap by using the networks
     *
     * @param show true to show the free / cheap information, false to not show it
     * @return this
     */
    public GDPRSetup withShowPaidOrFreeInfoText(boolean show) {
        mShowPaidOrFreeInfoText = show;
        return this;
    }

    /**
     * use this to use your own texts - only overwritten texts will be replaced!
     *
     * @param customTexts custom texts provider class
     * @return this
     */
    public GDPRSetup withCustomTexts(GDPRCustomTexts customTexts) {
        mCustomTexts = customTexts;
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

    public final GDPRLocationCheck[] requestLocationChecks() {
        return mRequestLocationChecks;
    }

    public final boolean needsPreperation() {
        return mRequestLocationChecks.length > 0 || mPublisherIds.size() > 0;
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

    public int connectionReadTimeout() {
        return mConnectionReadTimeout;
    }

    public int connectionConnectTimeout() {
        return mConnectionConnectTimeout;
    }

    public boolean showPaidOrFreeInfoText() {
        return mShowPaidOrFreeInfoText;
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

    public GDPRCustomTexts getCustomTexts() {
        return mCustomTexts;
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
        int requestLocationsCount = in.readInt();
        mRequestLocationChecks = new GDPRLocationCheck[requestLocationsCount];
        int[] requestLocations = new int[requestLocationsCount];
        if (requestLocationsCount > 0)
            in.readIntArray(requestLocations);
        for (int i = 0; i < requestLocationsCount; i++) {
           mRequestLocationChecks[i] = GDPRLocationCheck.values()[requestLocations[i]];
        }
        mUseBottomSheet = in.readByte() == 1;
        mForceSelection = in.readByte() == 1;
        mCustomDialogTheme = in.readInt();
        mShortQuestion = in.readByte() == 1;
        in.readStringList(mPublisherIds);
        mConnectionReadTimeout = in.readInt();
        mConnectionConnectTimeout = in.readInt();
        mShowPaidOrFreeInfoText = in.readByte() == 1;
        mCustomTexts = in.readParcelable(GDPRCustomTexts.class.getClassLoader());
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
        dest.writeInt(mRequestLocationChecks.length);
        if (mRequestLocationChecks.length > 0) {
            int[] requestLocations = new int[mRequestLocationChecks.length];
            for (int i = 0; i < mRequestLocationChecks.length; i++)
                requestLocations[i] = mRequestLocationChecks[i].ordinal();
            dest.writeIntArray(requestLocations);
        }
        dest.writeByte(mUseBottomSheet ? (byte) 1 : 0);
        dest.writeByte(mForceSelection ? (byte) 1 : 0);
        dest.writeInt(mCustomDialogTheme);
        dest.writeByte(mShortQuestion ? (byte) 1 : 0);
        dest.writeStringList(mPublisherIds);
        dest.writeInt(mConnectionReadTimeout);
        dest.writeInt(mConnectionConnectTimeout);
        dest.writeByte(mShowPaidOrFreeInfoText ? (byte) 1 : 0);
        dest.writeParcelable(mCustomTexts, 0);
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
