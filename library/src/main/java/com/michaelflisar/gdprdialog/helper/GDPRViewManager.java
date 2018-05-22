package com.michaelflisar.gdprdialog.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.R;

import java.util.ArrayList;
import java.util.List;

public class GDPRViewManager
{
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
            for (int i = 0; i< mSetup.networks().length; i++) {
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
        pages.add(view.findViewById(R.id.llPage2));
        pages.add(view.findViewById(R.id.llPage3));
        final Button btDisagree = view.findViewById(R.id.btDisagree);
        final Button btNoConsentAtAll = view.findViewById(R.id.btNoConsentAtAll);
        final Button btCloseAfterNoConsentAccepted = view.findViewById(R.id.btCloseAfterNoConsentAccepted);
        final TextView tvText1 = view.findViewById(R.id.tvText1);
        final TextView tvText2 = view.findViewById(R.id.tvText2);
        final TextView tvServices = view.findViewById(R.id.tvServices);
        final LinearLayout llServices = view.findViewById(R.id.llServices);
        final TextView tvTextNonPersonalAccepted = view.findViewById(R.id.tvTextNonPersonalAccepted);
        final TextView tvTextPersonalAccepted = view.findViewById(R.id.tvTextPersonalAccepted);
        final TextView tvTextNothingAccepted = view.findViewById(R.id.tvTextNothingAccepted);
        final TextView tvAdsInfo = view.findViewById(R.id.tvAdsInfo);
        final CheckBox cbAge = view.findViewById(R.id.cbAge);

        String text1 = activity.getString(R.string.gdpr_dialog_text_part1);
        String text2 = activity.getString(R.string.gdpr_dialog_text_part2, mSetup.explicitAgeConfirmation() ? activity.getString(R.string.gdpr_dialog_text_part2_no_age) : activity.getString(R.string.gdpr_dialog_text_part2_with_age));
        final String withdrawConsentInfoAddon = activity.getString(R.string.gdpr_withdraw_consent_info_addon);
        tvText1.setText(Html.fromHtml(text1));
        tvText2.setText(Html.fromHtml(text2));
        tvTextNonPersonalAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_non_personal, mSetup.getNetworksCommaSeperated(activity,false), withdrawConsentInfoAddon)));
        tvTextPersonalAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_personal, withdrawConsentInfoAddon)));
        if (mSetup.hasPaidVersion()) {
            tvTextNothingAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_nothing_paid_version_needed)));
            btCloseAfterNoConsentAccepted.setText(R.string.gdpr_buy_app);
        } else {
            tvTextNothingAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_nothing, withdrawConsentInfoAddon)));
        }

        // we only accept this flag if more than one networks are used
        if (mSetup.explicitConsentForEachService() && mSetup.networks().length > 1) {
            tvServices.setVisibility(View.GONE);
            LayoutInflater inflater = LayoutInflater.from(activity);
            for (int i = 0; i< mSetup.networks().length; i++) {
                View row = inflater.inflate(R.layout.gdpr_consent_row, null);
                CheckBox cb = row.findViewById(R.id.cbCheckbox);
                cb.setChecked(mExplicitlyConfirmedServices.get(i) == 1);
                int finalI = i;
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> mExplicitlyConfirmedServices.set(finalI, isChecked ? 1 : 0));
                TextView tv = row.findViewById(R.id.tvText);
                tv.setText(Html.fromHtml(mSetup.networks()[i].getCheckboxHtmlLink(activity)));
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                llServices.addView(row);
            }
        } else {
            String textServices = mSetup.getNetworksCommaSeperated(activity, true);
            tvServices.setText(Html.fromHtml(textServices));
            llServices.setVisibility(View.GONE);
        }

        if (!mSetup.containsAdNetwork() || (mSetup.hasPaidVersion() && !mSetup.allowNonPersonalisedForPaidVersion())) {
            tvAdsInfo.setVisibility(View.GONE);
        }

        if (!mSetup.explicitAgeConfirmation()) {
            cbAge.setVisibility(View.GONE);
        } else {
            cbAge.setChecked(mAgeConfirmed);
            cbAge.setOnCheckedChangeListener((buttonView, isChecked) -> mAgeConfirmed = isChecked);
        }

        if (mSetup.hasPaidVersion()) {
            if (!mSetup.allowNonPersonalisedForPaidVersion()) {
                btDisagree.setText(R.string.gdpr_dialog_disagree_buy_add);
            } else {
                btNoConsentAtAll.setText(R.string.gdpr_dialog_disagree_buy_add);
            }
        }

        tvServices.setMovementMethod(LinkMovementMethod.getInstance());
        tvTextNonPersonalAccepted.setMovementMethod(LinkMovementMethod.getInstance());

        updateSelectedPage(pages, view);

        // ------------------
        // Step 0 - Info Page
        // ------------------

        view.findViewById(R.id.btAgree).setOnClickListener(v -> {
            if (!isAgeValid(v.getContext(), true) || !isAllConsentGiven(v.getContext(), true)) {
                return;
            }
            mSelectedConsent = GDPRConsent.PERSONAL_CONSENT;
            mCurrentStep = 1;
            updateSelectedPage(pages, view);
        });

        view.findViewById(R.id.btDisagree).setOnClickListener(v -> {
            if (!isAgeValid(v.getContext(), false)  || !isAllConsentGiven(v.getContext(), false)) {
                return;
            }
            if (mSetup.hasPaidVersion()) {
                if (mSetup.allowNonPersonalisedForPaidVersion()) {
                    mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
                    mCurrentStep = 2;
                } else {
                    mSelectedConsent = GDPRConsent.NO_CONSENT;
                    onFinish(onFinishViewListener);
                    return;
                }
            } else {
                mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
                mCurrentStep = 2;
            }
            updateSelectedPage(pages, view);
        });

        if (!mSetup.allowAnyNoConsent()) {
            btNoConsentAtAll.setVisibility(View.GONE);
        } else {
            btNoConsentAtAll.setOnClickListener(v -> {
                mSelectedConsent = GDPRConsent.NO_CONSENT;
                mCurrentStep = 3;
                updateSelectedPage(pages, view);
            });
        }

        // ------------------
        // Step 1 - User accepted personal ads page
        // ------------------

        view.findViewById(R.id.btCloseAccepted).setOnClickListener(v -> onFinish(onFinishViewListener));

        // ------------------
        // Step 2 - User did not accept personal ads page
        // ------------------

        view.findViewById(R.id.btCloseAcceptedNonPersonal).setOnClickListener(v -> {
            onFinish(onFinishViewListener);
        });

        // ------------------
        // Step 3 - User did not accept any ads page
        // ------------------

        btCloseAfterNoConsentAccepted.setOnClickListener(v -> {
            onFinish(onFinishViewListener);
        });
    }

    public void reset() {
        mCallback = null;
    }

    // ---------------
    // Helper function
    // ---------------

    private boolean isAgeValid(Context context, boolean agree) {
        if (mSetup.explicitAgeConfirmation()) {
            // we only need to check age for personalised ads
            if ((agree && !mAgeConfirmed)) {// || (!agree && mSetup.hasPaidVersion() && mSetup.allowNonPersonalisedForPaidVersion())) {
                Toast.makeText(context, R.string.gdpr_age_not_confirmed, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean isAllConsentGiven(Context context, boolean agree) {
        if (mSetup.explicitConsentForEachService() && agree) {
            int consentsGiven = 0;
            for (int i = 0; i < mExplicitlyConfirmedServices.size(); i++) {
                if (mExplicitlyConfirmedServices.get(i) == 1) {
                    consentsGiven++;
                }
            }
            if (mSetup.networks().length != consentsGiven) {
                Toast.makeText(context, R.string.gdpr_not_all_services_accepted, Toast.LENGTH_LONG).show();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void updateSelectedPage(List<LinearLayout> pages, View view) {
        for (int i = 0; i < pages.size(); i++) {
            pages.get(i).setVisibility(i == mCurrentStep ? View.VISIBLE : View.GONE);
        }
        // TODO: resize dialog...
        // view.requestLayout();
    }

    private void onFinish(IOnFinishView onFinishView) {
        if (mSelectedConsent != null) {
            GDPR.getInstance().setConsent(mSelectedConsent);
            mCallback.onConsentInfoUpdate(mSelectedConsent, true);
        }
        onFinishView.onFinishView();
    }

    // ---------------
    // Interface
    // ---------------

    public interface IOnFinishView {
        void onFinishView();
    }
}
