package com.michaelflisar.gdprdialog.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRConsentState;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.R;

import java.util.ArrayList;
import java.util.List;

public class GDPRViewManager {

    public static String ARG_SETUP = "ARG_SETUP";
    public static String ARG_LOCATION = "ARG_LOCATION";

    public static Bundle createBundle(GDPRSetup setup, GDPRLocation location) {
        Bundle args = new Bundle();
        args.putParcelable(GDPRViewManager.ARG_SETUP, setup);
        args.putInt(GDPRViewManager.ARG_LOCATION, location.ordinal());
        return args;
    }

    private static String KEY_STEP = "KEY_STEP";
    private static String KEY_AGE_CONFIRMED = "KEY_AGE_CONFIRMED";
    private static String KEY_SELECTED_CONSENT = "KEY_SELECTED_CONSENT";
    private static String KEY_EXPLICITLY_CONFIRMED_SERVICES = "KEY_EXPLICITLY_CONFIRMED_SERVICES";

    private GDPRSetup mSetup;
    private GDPRLocation mLocation;
    private GDPR.IGDPRCallback mCallback = null;

    private int mCurrentStep = 0;
    private GDPRConsent mSelectedConsent = null;
    private boolean mAgeConfirmed = false;
    private ArrayList<Integer> mExplicitlyConfirmedServices = new ArrayList<>();

    private Snackbar mSnackbar = null;

    private final List<LinearLayout> mPages = new ArrayList<>();

    public GDPRViewManager(Bundle args, Bundle savedInstanceState) {
        args.setClassLoader(GDPRSetup.class.getClassLoader());
        mSetup = args.getParcelable(ARG_SETUP);
        mLocation = GDPRLocation.values()[args.getInt(ARG_LOCATION)];
        if (savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getInt(KEY_STEP);
            if (savedInstanceState.containsKey(KEY_SELECTED_CONSENT)) {
                mSelectedConsent = GDPRConsent.values()[savedInstanceState.getInt(KEY_SELECTED_CONSENT)];
            }
            mAgeConfirmed = savedInstanceState.getBoolean(KEY_AGE_CONFIRMED);
            mExplicitlyConfirmedServices = savedInstanceState.getIntegerArrayList(KEY_EXPLICITLY_CONFIRMED_SERVICES);
        } else {
            mExplicitlyConfirmedServices.clear();
            for (int i = 0; i < mSetup.networks().length; i++) {
                mExplicitlyConfirmedServices.add(0);
            }
        }
    }

    public void save(Bundle outState) {
        outState.putInt(KEY_STEP, mCurrentStep);
        if (mSelectedConsent != null) {
            outState.putInt(KEY_SELECTED_CONSENT, mSelectedConsent.ordinal());
        }
        outState.putBoolean(KEY_AGE_CONFIRMED, mAgeConfirmed);
        outState.putIntegerArrayList(KEY_EXPLICITLY_CONFIRMED_SERVICES, mExplicitlyConfirmedServices);
    }

    public void setCallback(Object callback) {
        setCallback(callback, true);
    }

    public void setCallback(Object callback, boolean forceActivityToImplementCallback) {
        try {
            mCallback = (GDPR.IGDPRCallback) callback;
        } catch (ClassCastException e) {
            if (forceActivityToImplementCallback) {
                throw new ClassCastException("Parent activity must implement GDPR.IGDPRCallback interface!");
            } else {
                GDPR.getInstance().getLogger().debug("GDPRViewManager", "Activity is not implementing callback, but this is explicitly demanded by the user");
            }
        }
    }

    public GDPRConsent getSelectedConsent() {
        return mSelectedConsent;
    }

    public GDPRSetup getSetup() {
        return mSetup;
    }

    public GDPR.IGDPRCallback getCallback() {
        return mCallback;
    }

    public boolean shouldCloseApp() {
        // only close app if nothing is selected, which means forceSelect is not enabled and user pressed back
        return mSelectedConsent == null;// || (mSelectedConsent == GDPRConsent.NO_CONSENT && !mSetup.hasPaidVersion());
    }

    public boolean shouldUseBottomSheet() {
        return mSetup.useBottomSheet();
    }

    public void init(Activity activity, View view, IOnFinishView onFinishViewListener) {
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setVisibility((shouldUseBottomSheet() || mSetup.noToolbarTheme()) ? View.VISIBLE : View.GONE);
        if (mSetup.getCustomTexts().hasTitle())
            toolbar.setTitle(mSetup.getCustomTexts().getTitle(view.getContext()));
        else
            toolbar.setTitle(R.string.gdpr_dialog_title);

        mPages.add(view.findViewById(R.id.llPage0));
        mPages.add(view.findViewById(R.id.llPage1));
        mPages.add(view.findViewById(R.id.llPage2));

        // general page
        Button btAgree = view.findViewById(R.id.btAgree);
        final Button btDisagree = view.findViewById(R.id.btDisagree);
        final Button btNoConsentAtAll = view.findViewById(R.id.btNoConsentAtAll);
        final TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        final TextView tvText1 = view.findViewById(R.id.tvText1);
        final TextView tvText2 = view.findViewById(R.id.tvText2);
        final TextView tvText3 = view.findViewById(R.id.tvText3);
        final CheckBox cbAge = view.findViewById(R.id.cbAge);

        initGeneralTexts(activity, tvQuestion, tvText1, tvText2, tvText3, cbAge);
        initButtons(activity, btAgree, btDisagree, btNoConsentAtAll);

        // info page
        final TextView tvServiceInfo1 = view.findViewById(R.id.tvServiceInfo1);
        final TextView tvServiceInfo2 = view.findViewById(R.id.tvServiceInfo2);
        final TextView tvServiceInfo3 = view.findViewById(R.id.tvServiceInfo3);

        initInfoTexts(activity, tvServiceInfo1, tvServiceInfo2, tvServiceInfo3);

        updateSelectedPage();

        // ------------------
        // Step 0 - general page
        // ------------------

        btAgree.setOnClickListener(v -> {
            if (!isAgeValid(view, true) || !isAllConsentGiven(view, true)) {
                return;
            }
            mSelectedConsent = GDPRConsent.PERSONAL_CONSENT;
            onFinish(activity, onFinishViewListener);
        });

        btDisagree.setOnClickListener(v -> {
            if (!isAgeValid(view, false) || !isAllConsentGiven(view, false)) {
                return;
            }
            if (mSetup.hasPaidVersion()) {
                if (mSetup.allowNonPersonalisedForPaidVersion()) {
                    if (mSetup.explicitNonPersonalisedConfirmation()) {
                        mCurrentStep = 2;
                        updateSelectedPage();
                        return;
                    }
                    mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
                    onFinish(activity, onFinishViewListener);
                } else {
                    mSelectedConsent = GDPRConsent.NO_CONSENT;
                    onFinish(activity, onFinishViewListener);
                }
            } else {
                if (mSetup.explicitNonPersonalisedConfirmation()) {
                    mCurrentStep = 2;
                    updateSelectedPage();
                    return;
                }
                mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
                onFinish(activity, onFinishViewListener);
            }
        });

        if (!mSetup.allowAnyNoConsent()) {
            btNoConsentAtAll.setVisibility(View.GONE);
        } else {
            btNoConsentAtAll.setOnClickListener(v -> {
                mSelectedConsent = GDPRConsent.NO_CONSENT;
                onFinish(activity, onFinishViewListener);
            });
        }

        // ------------------
        // Step 1 - info page
        // ------------------

        view.findViewById(R.id.btBack).setOnClickListener(v -> {
            mCurrentStep = 0;
            updateSelectedPage();
        });

        // ------------------
        // Step 2 - expicit non personalised consent page
        // ------------------

        view.findViewById(R.id.btAgreeNonPersonalised).setOnClickListener(v -> {
            mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
            onFinish(activity, onFinishViewListener);
        });
    }

    public void initActionBar(Activity activity, ActionBar supportActionBar) {
        if (mSetup.getCustomTexts().hasTitle())
            supportActionBar.setTitle(mSetup.getCustomTexts().getTitle(activity));
        else
            supportActionBar.setTitle(R.string.gdpr_dialog_title);
    }

    private void initButtons(Activity activity, Button btAgree, Button btDisagree, Button btNoConsentAtAll) {

        if (mSetup.hasPaidVersion()) {
            if (!mSetup.allowNonPersonalisedForPaidVersion()) {
                btDisagree.setText(R.string.gdpr_dialog_disagree_buy_app);
            } else {
                btNoConsentAtAll.setText(R.string.gdpr_dialog_disagree_buy_app);
            }
        }

        boolean hideAdsInfo = !mSetup.containsAdNetwork();
        if (mSetup.hasPaidVersion()) {
            if (!mSetup.allowNonPersonalisedForPaidVersion()) {
                btDisagree.setText(R.string.gdpr_dialog_disagree_buy_app);
                hideAdsInfo = true;
            }
        }

        if (!hideAdsInfo) {
            // upper case + bold button style is removed and handled manually
            String textButton = activity.getString(R.string.gdpr_dialog_disagree_no_thanks).toUpperCase() + "\n";
            String textInfo = activity.getString(R.string.gdpr_dialog_disagree_info);
            SpannableString spannableText = new SpannableString(textButton + textInfo);
            spannableText.setSpan(new StyleSpan(Typeface.BOLD), 0, textButton.length(), 0);
            spannableText.setSpan(new RelativeSizeSpan(0.8f), textButton.length(), spannableText.length(), 0);
            spannableText.setSpan(new ForegroundColorSpan(btDisagree.getTextColors().getDefaultColor()), textButton.length(), spannableText.length(), 0);
            btDisagree.setAllCaps(false);
            btDisagree.setTypeface(Typeface.DEFAULT);
            btDisagree.setText(spannableText);
        }

//        int textColorPrimary = GDPRUtils.getThemeColor(context, android.R.attr.textColorPrimary);
//        boolean textColorIsDark = GDPRUtils.isColorDark(textColorPrimary);
//
//        int btBackgorundColor = textColorIsDark ? Color.DKGRAY : Color.WHITE;
//        int btForegorundColor = textColorIsDark ? Color.WHITE : Color.BLACK;
//
//        btDisagree.getBackground().setColorFilter(btForegorundColor, PorterDuff.Mode.MULTIPLY);
//        btNoConsentAtAll.getBackground().setColorFilter(btForegorundColor, PorterDuff.Mode.MULTIPLY);
//        btDisagree.setTextColor(btBackgorundColor);
//        btNoConsentAtAll.setTextColor(btBackgorundColor);
    }

    private void initGeneralTexts(Activity activity, TextView tvQuestion, TextView tvText1, TextView tvText2, TextView tvText3, CheckBox cbAge) {

        if (mSetup.getCustomTexts().hasQuestion()) {
            tvQuestion.setText(mSetup.getCustomTexts().getQuestion(activity));
        } else {
            String question = activity.getString(R.string.gdpr_dialog_question, (mSetup.containsAdNetwork() && !mSetup.shortQuestion()) ? activity.getString(R.string.gdpr_dialog_question_ads_info) : "");
            tvQuestion.setText(Html.fromHtml(question));
        }

        if (mSetup.getCustomTexts().hasTopText()) {
            tvText1.setText(Html.fromHtml(mSetup.getCustomTexts().getTopText(activity)));
        } else {
            String cheapOrFree = activity.getString(mSetup.hasPaidVersion() ? R.string.gdpr_cheap : R.string.gdpr_free);
            String text1 = activity.getString(R.string.gdpr_dialog_text1_part1);
            if (mSetup.showPaidOrFreeInfoText())
            {
                text1 += " " + activity.getString(R.string.gdpr_dialog_text1_part2, cheapOrFree);
            }
            tvText1.setText(Html.fromHtml(text1));
        }
        tvText1.setMovementMethod(LinkMovementMethod.getInstance());

        if (mSetup.getCustomTexts().hasMainText()) {
            tvText2.setText(mSetup.getCustomTexts().getMainText(activity));
        } else {
            int typesCount = mSetup.getNetworkTypes().size();
            String types = mSetup.getNetworkTypesCommaSeperated(activity);
            String text2 = typesCount == 1 ?
                    activity.getString(R.string.gdpr_dialog_text2_singular, types) :
                    activity.getString(R.string.gdpr_dialog_text2_plural, types);
            CharSequence sequence2 = Html.fromHtml(text2);
            SpannableStringBuilder strBuilder2 = new SpannableStringBuilder(sequence2);
            URLSpan[] urls = strBuilder2.getSpans(0, sequence2.length(), URLSpan.class);
            for (URLSpan span : urls)
            {
                makeLinkClickable(strBuilder2, span, () -> {
                    mCurrentStep = 1;
                    updateSelectedPage();
                });
            }
            tvText2.setText(strBuilder2);
        }
        tvText2.setMovementMethod(LinkMovementMethod.getInstance());

        if (mSetup.getCustomTexts().hasAgeMsg()) {
            tvText3.setText(mSetup.getCustomTexts().getAgeMsg(activity));
        } else {

            String text3 = activity.getString(R.string.gdpr_dialog_text3);
            tvText3.setText(Html.fromHtml(text3));
        }
        tvText3.setMovementMethod(LinkMovementMethod.getInstance());

        if (!mSetup.explicitAgeConfirmation()) {
            cbAge.setVisibility(View.GONE);
        } else {
            tvText3.setVisibility(View.GONE);
            cbAge.setChecked(mAgeConfirmed);
            cbAge.setOnCheckedChangeListener((buttonView, isChecked) -> mAgeConfirmed = isChecked);
        }

        //GDPRUtils.justify(tvText1);
        justifyText(tvText2);
        //GDPRUtils.justify(tvText3);
        //GDPRUtils.justify(tvQuestion);
    }

    private void initInfoTexts(Activity activity, TextView tvServiceInfo1, TextView tvServiceInfo2, TextView tvServiceInfo3) {

        String textInfo2 = mSetup.getNetworksCommaSeperated(activity, true);
        tvServiceInfo2.setText(Html.fromHtml(textInfo2));
        tvServiceInfo2.setMovementMethod(LinkMovementMethod.getInstance());

        String privacyPolicyPart = mSetup.policyLink() == null ? "" : activity.getString(R.string.gdpr_dialog_text_info3_privacy_policy_part, mSetup.policyLink());
        String textInfo3 = activity.getString(R.string.gdpr_dialog_text_info3, privacyPolicyPart);
        tvServiceInfo3.setText(Html.fromHtml(textInfo3));
        tvServiceInfo3.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void reset() {
        GDPR.getInstance().cancelRunningTasks();
        mCallback = null;
        mPages.clear();
    }

    // ---------------
    // Helper function
    // ---------------

    private boolean isAgeValid(View view, boolean agree) {
        if (mSetup.explicitAgeConfirmation()) {
            // we only need to check age for personalised ads
            if ((agree && !mAgeConfirmed)) {// || (!agree && mSetup.hasPaidVersion() && mSetup.allowNonPersonalisedForPaidVersion())) {
                showSnackbar(R.string.gdpr_age_not_confirmed, view);
                return false;
            }
        }
        return true;
    }

    private boolean isAllConsentGiven(View view, boolean agree) {
        return true;
    }

    private void updateSelectedPage() {
        for (int i = 0; i < mPages.size(); i++) {
            mPages.get(i).setVisibility(i == mCurrentStep ? View.VISIBLE : View.GONE);
        }
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    private void onFinish(Context context, IOnFinishView onFinishView) {
        if (mSelectedConsent != null) {
            GDPRConsentState consentState = new GDPRConsentState(context, mSelectedConsent, mLocation);
            GDPR.getInstance().setConsent(consentState);
            if (mCallback != null) {
                mCallback.onConsentInfoUpdate(consentState, true);
            }
        }
        onFinishView.onFinishView();
    }

    private void showSnackbar(int message, View view) {
        // TODO: for some reason, the snackbar does not show in the bottom sheet
        if (mSetup.useBottomSheet()) {
            Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
        } else {
            mSnackbar = Snackbar
                    .make(view, message, Snackbar.LENGTH_LONG);
            mSnackbar.show();
        }
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span, Runnable runnable) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                runnable.run();
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public boolean handleBackPress() {
        if (mCurrentStep > 0) {
            mCurrentStep = 0;
            updateSelectedPage();
            return true;
        } else {
            return false;
        }
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    private void justifyText(TextView textView) {
        // does not work good enough, check out this: https://github.com/MFlisar/GDPRDialog/issues/21
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            textView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
//        } else
        // does not work good enough either, check out this: https://github.com/MFlisar/GDPRDialog/issues/43
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // not perfect, but better than nothing
//            textView.setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED);
//            // wrap content is not working with this strategy, so we wait for the layout
//            // and find the longest line and use it's width for the textview and then center the layout
//            textView.post(() -> {
//                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)textView.getLayoutParams();
//                lp.width = (int)getMaxLineWidth(textView); //LinearLayout.LayoutParams.WRAP_CONTENT;
//                lp.gravity = Gravity.CENTER_HORIZONTAL;
//                textView.setLayoutParams(lp);
//            });
//        } else {
//            // sorry, not supported...
//        }
    }

    private float getMaxLineWidth(TextView textView) {
        Layout layout = textView.getLayout();
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }

    // ---------------
    // Interface
    // ---------------

    public interface IOnFinishView {
        void onFinishView();
    }
}
