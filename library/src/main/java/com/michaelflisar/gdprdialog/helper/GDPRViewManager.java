package com.michaelflisar.gdprdialog.helper;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.R;

import java.util.ArrayList;
import java.util.List;

public class GDPRViewManager {
    public static String ARG_SETUP = "ARG_SETUP";

    private static String KEY_STEP = "KEY_STEP";
    private static String KEY_AGE_CONFIRMED = "KEY_AGE_CONFIRMED";
    private static String KEY_SELECTED_CONSENT = "KEY_SELECTED_CONSENT";
    private static String KEY_EXPLICITLY_CONFIRMED_SERVICES = "KEY_EXPLICITLY_CONFIRMED_SERVICES";

    private GDPRSetup mSetup;
    private GDPR.IGDPRCallback mCallback = null;

    private int mCurrentStep = 0;
    private GDPRConsent mSelectedConsent = null;
    private boolean mAgeConfirmed = false;
    private ArrayList<Integer> mExplicitlyConfirmedServices = new ArrayList<>();

    private Snackbar mSnackbar = null;

    public GDPRViewManager(Bundle args, Bundle savedInstanceState) {
        mSetup = args.getParcelable(ARG_SETUP);
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
        try {
            mCallback = (GDPR.IGDPRCallback) callback;
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent activity must implement GDPR.IGDPRCallback interface!");
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
        return mSelectedConsent == null || (mSelectedConsent == GDPRConsent.NO_CONSENT && !mSetup.allowAnyNoConsent());
    }

    public void init(Activity activity, View view, IOnFinishView onFinishViewListener) {
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setVisibility(mSetup.noToolbarTheme() ? View.VISIBLE : View.GONE);
        toolbar.setTitle(R.string.gdpr_dialog_title);
        final List<LinearLayout> pages = new ArrayList<>();
        pages.add(view.findViewById(R.id.llPage0));
        pages.add(view.findViewById(R.id.llPage1));

        // general page
        final Button btDisagree = view.findViewById(R.id.btDisagree);
        final Button btNoConsentAtAll = view.findViewById(R.id.btNoConsentAtAll);
        final TextView tvText1 = view.findViewById(R.id.tvText1);
        final TextView tvText2 = view.findViewById(R.id.tvText2);
        final TextView tvText3 = view.findViewById(R.id.tvText3);
        final TextView tvText4 = view.findViewById(R.id.tvText4);
        final CheckBox cbAge = view.findViewById(R.id.cbAge);
        final TextView tvAdsInfo = view.findViewById(R.id.tvAdsInfo);

        initGeneralTexts(activity, pages, tvText1, tvText2, tvText3, tvText4, cbAge, tvAdsInfo);

        // info page
        final TextView tvServiceInfo1 = view.findViewById(R.id.tvServiceInfo1);
        final TextView tvServiceInfo2 = view.findViewById(R.id.tvServiceInfo2);
        final TextView tvServiceInfo3 = view.findViewById(R.id.tvServiceInfo3);

        initInfoTexts(activity, tvServiceInfo1, tvServiceInfo2, tvServiceInfo3);


//        tvTextNonPersonalAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_non_personal, mSetup.getNetworksCommaSeperated(activity, false), withdrawConsentInfoAddon)));
//        tvTextPersonalAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_personal, withdrawConsentInfoAddon)));
//        if (mSetup.hasPaidVersion()) {
//            tvTextNothingAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_nothing_paid_version_needed)));
//            btCloseAfterNoConsentAccepted.setText(R.string.gdpr_buy_app);
//        } else {
//            tvTextNothingAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_nothing, withdrawConsentInfoAddon)));
//        }

        // we only accept this flag if more than one networks are used
//        if (mSetup.explicitConsentForEachService() && mSetup.networks().length > 1) {
//            tvServices.setVisibility(View.GONE);
//            LayoutInflater inflater = LayoutInflater.from(activity);
//            for (int i = 0; i < mSetup.networks().length; i++) {
//                View row = inflater.inflate(R.layout.gdpr_consent_row, null);
//                CheckBox cb = row.findViewById(R.id.cbCheckbox);
//                cb.setChecked(mExplicitlyConfirmedServices.get(i) == 1);
//                int finalI = i;
//                cb.setOnCheckedChangeListener((buttonView, isChecked) -> mExplicitlyConfirmedServices.set(finalI, isChecked ? 1 : 0));
//                TextView tv = row.findViewById(R.id.tvText);
//                tv.setText(Html.fromHtml(mSetup.networks()[i].getCheckboxHtmlLink(activity)));
//                tv.setMovementMethod(LinkMovementMethod.getInstance());
//                llServices.addView(row);
//            }
//        } else {
//            String textServices = mSetup.getNetworksCommaSeperated(activity, true);
//            tvServices.setText(Html.fromHtml(textServices));
//            llServices.setVisibility(View.GONE);
//        }





        if (mSetup.hasPaidVersion()) {
            if (!mSetup.allowNonPersonalisedForPaidVersion()) {
                btDisagree.setText(R.string.gdpr_dialog_disagree_buy_add);
            } else {
                btNoConsentAtAll.setText(R.string.gdpr_dialog_disagree_buy_add);
            }
        }

//        tvTextNonPersonalAccepted.setMovementMethod(LinkMovementMethod.getInstance());

        updateSelectedPage(pages);

        // ------------------
        // Step 0 - general page
        // ------------------

        view.findViewById(R.id.btAgree).setOnClickListener(v -> {
            if (!isAgeValid(view, true) || !isAllConsentGiven(view, true)) {
                return;
            }
            mSelectedConsent = GDPRConsent.PERSONAL_CONSENT;
            onFinish(onFinishViewListener);
        });

        view.findViewById(R.id.btDisagree).setOnClickListener(v -> {
            if (!isAgeValid(view, false) || !isAllConsentGiven(view, false)) {
                return;
            }
            if (mSetup.hasPaidVersion()) {
                if (mSetup.allowNonPersonalisedForPaidVersion()) {
                    mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
                    onFinish(onFinishViewListener);
                } else {
                    mSelectedConsent = GDPRConsent.NO_CONSENT;
                    onFinish(onFinishViewListener);
                }
            } else {
                mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
                onFinish(onFinishViewListener);
            }
        });

        if (!mSetup.allowAnyNoConsent()) {
            btNoConsentAtAll.setVisibility(View.GONE);
        } else {
            btNoConsentAtAll.setOnClickListener(v -> {
                mSelectedConsent = GDPRConsent.NO_CONSENT;
                onFinish(onFinishViewListener);
            });
        }

        // ------------------
        // Step 1 - info page
        // ------------------

        view.findViewById(R.id.btBack).setOnClickListener(v -> {
            mCurrentStep = 0;
            updateSelectedPage(pages);
        });
    }

    private void initGeneralTexts(Activity activity, List<LinearLayout> pages, TextView tvText1, TextView tvText2, TextView tvText3, TextView tvText4, CheckBox cbAge, TextView tvAdsInfo) {

        String cheapOrFree = activity.getString(mSetup.hasPaidVersion() ? R.string.gdpr_cheap : R.string.gdpr_free);
        String text1 = activity.getString(R.string.gdpr_dialog_text1, cheapOrFree);
        tvText1.setText(Html.fromHtml(text1));
        tvText1.setMovementMethod(LinkMovementMethod.getInstance());

        int typesCount = mSetup.getNetworkTypes().size();
        String types = mSetup.getNetworkTypesCommaSeperated(activity);
        String thisOrThose = activity.getResources().getQuantityString(R.plurals.thisMiddle, typesCount);
        String thisOrThoseBeginning = activity.getResources().getQuantityString(R.plurals.thisFirst, typesCount);
        String serviceOrServices = activity.getResources().getQuantityString(R.plurals.service, typesCount);
        String collectOrCollects = activity.getResources().getQuantityString(R.plurals.collect, typesCount);
        String their = activity.getResources().getQuantityString(R.plurals.their, typesCount);
        String text2 = activity.getString(R.string.gdpr_dialog_text2, types, thisOrThoseBeginning, serviceOrServices, collectOrCollects, thisOrThose, their);
        CharSequence sequence2 = Html.fromHtml(text2);
        SpannableStringBuilder strBuilder2 = new SpannableStringBuilder(sequence2);
        URLSpan[] urls = strBuilder2.getSpans(0, sequence2.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(strBuilder2, span, () -> {
                mCurrentStep = 1;
                updateSelectedPage(pages);
            });
        }
        tvText2.setText(strBuilder2);
        tvText2.setMovementMethod(LinkMovementMethod.getInstance());

        String text3 = activity.getString(R.string.gdpr_dialog_text3);
        tvText3.setText(Html.fromHtml(text3));
        tvText3.setMovementMethod(LinkMovementMethod.getInstance());

        String text4 = activity.getString(R.string.gdpr_dialog_text4);
        tvText4.setText(Html.fromHtml(text4));
        tvText4.setMovementMethod(LinkMovementMethod.getInstance());

        if (!mSetup.explicitAgeConfirmation()) {
            cbAge.setVisibility(View.GONE);
        } else {
            tvText4.setVisibility(View.GONE);
            cbAge.setChecked(mAgeConfirmed);
            cbAge.setOnCheckedChangeListener((buttonView, isChecked) -> mAgeConfirmed = isChecked);
        }

        if (!mSetup.containsAdNetwork() || (mSetup.hasPaidVersion() && !mSetup.allowNonPersonalisedForPaidVersion())) {
            tvAdsInfo.setVisibility(View.GONE);
        }
    }

    private void initInfoTexts(Activity activity, TextView tvServiceInfo1, TextView tvServiceInfo2, TextView tvServiceInfo3) {

        String textInfo2 = mSetup.getNetworksCommaSeperated(activity, true);
        tvServiceInfo2.setText(Html.fromHtml(textInfo2));
        tvServiceInfo2.setMovementMethod(LinkMovementMethod.getInstance());

        String textInfo3 = activity.getString(R.string.gdpr_dialog_text_info3,  mSetup.policyLink());
        tvServiceInfo3.setText(Html.fromHtml(textInfo3));
        tvServiceInfo3.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void reset() {
        mCallback = null;
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

    private void updateSelectedPage(List<LinearLayout> pages) {
        for (int i = 0; i < pages.size(); i++) {
            pages.get(i).setVisibility(i == mCurrentStep ? View.VISIBLE : View.GONE);
        }
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    private void onFinish(IOnFinishView onFinishView) {
        if (mSelectedConsent != null) {
            GDPR.getInstance().setConsent(mSelectedConsent);
            mCallback.onConsentInfoUpdate(mSelectedConsent, true);
        }
        onFinishView.onFinishView();
    }

    private void showSnackbar(int message, View view) {
        mSnackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        mSnackbar.show();
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


    // ---------------
    // Interface
    // ---------------

    public interface IOnFinishView {
        void onFinishView();
    }
}
