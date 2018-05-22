package com.michaelflisar.gdprdialog.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.R;

public class GDPRViewManager
{
    public static String ARG_SETUP = "ARG_SETUP";

    private static String KEY_STEP = "KEY_STEP";
    private static String KEY_AGE_CONFIRMED = "KEY_AGE_CONFIRMED";
    private static String KEY_SELECTED_CONSENT = "KEY_SELECTED_CONSENT";

    private GDPRSetup mSetup;
    private GDPR.IGDPRCallback mCallback = null;

    private int mCurrentStep = 0;
    private GDPRConsent mSelectedConsent = null;
    private boolean mAgeConfirmed = false;

    public GDPRViewManager(Bundle args, Bundle savedInstanceState) {
        mSetup = args.getParcelable(ARG_SETUP);
        if (savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getInt(KEY_STEP);
            if (savedInstanceState.containsKey(KEY_SELECTED_CONSENT)) {
                mSelectedConsent = GDPRConsent.values()[savedInstanceState.getInt(KEY_SELECTED_CONSENT)];
            }
            mAgeConfirmed = savedInstanceState.getBoolean(KEY_AGE_CONFIRMED);
        }
    }

    public void save(Bundle outState) {
        outState.putInt(KEY_STEP, mCurrentStep);
        if (mSelectedConsent != null) {
            outState.putInt(KEY_SELECTED_CONSENT, mSelectedConsent.ordinal());
        }
        outState.putBoolean(KEY_AGE_CONFIRMED, mAgeConfirmed);
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
        return mSelectedConsent == null || (mSelectedConsent == GDPRConsent.NO_CONSENT && mSetup.allowNoConsent());
    }

    public void init(Activity activity, View view, IOnFinishView onFinishViewListener) {
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setVisibility(mSetup.noToolbarTheme() ? View.VISIBLE : View.GONE);
        toolbar.setTitle(R.string.gdpr_dialog_title);
        final ViewFlipper vfFlipper = view.findViewById(R.id.vfFlipper);
        final Button btDisagree = view.findViewById(R.id.btDisagree);
        final Button btNoConsentAtAll = view.findViewById(R.id.btNoConsentAtAll);
        final Button btCloseAfterNoConsentAccepted = view.findViewById(R.id.btCloseAfterNoConsentAccepted);
        final TextView tvText = view.findViewById(R.id.tvText);
        final TextView tvTextNonPersonalAccepted = view.findViewById(R.id.tvTextNonPersonalAccepted);
        final TextView tvTextPersonalAccepted = view.findViewById(R.id.tvTextPersonalAccepted);
        final TextView tvTextNothingAccepted = view.findViewById(R.id.tvTextNothingAccepted);
        final TextView tvAdsInfo = view.findViewById(R.id.tvAdsInfo);
        final CheckBox cbAge = view.findViewById(R.id.cbAge);
        String text = activity.getString(R.string.gdpr_dialog_text_part1, mSetup.getNetworksCommaSeperated(activity, true));
        if (mSetup.askForAgeConfirmation()) {
            text += activity.getString(R.string.gdpr_dialog_text_part2_no_age);
        } else {
            text += activity.getString(R.string.gdpr_dialog_text_part2_with_age);
        }
        final String withdrawConsentInfoAddon = activity.getString(R.string.gdpr_withdraw_consent_info_addon);
        tvText.setText(Html.fromHtml(text));
        tvTextNonPersonalAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_non_personal, mSetup.getNetworksCommaSeperated(activity,false), withdrawConsentInfoAddon)));
        tvTextPersonalAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_personal, withdrawConsentInfoAddon)));
        if (mSetup.hasPaidVersion()) {
            tvTextNothingAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_nothing_paid_version_needed)));
            btCloseAfterNoConsentAccepted.setText(R.string.gdpr_buy_app);
        } else {
            tvTextNothingAccepted.setText(Html.fromHtml(activity.getString(R.string.gdpr_dialog_text_after_accepted_nothing, withdrawConsentInfoAddon)));
        }

        if (!mSetup.containsAdNetwork() || (mSetup.hasPaidVersion() && !mSetup.allowNonPersonalisedForPaidVersion())) {
            tvAdsInfo.setVisibility(View.GONE);
        }

        if (!mSetup.askForAgeConfirmation()) {
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

        tvText.setMovementMethod(LinkMovementMethod.getInstance());
        tvTextNonPersonalAccepted.setMovementMethod(LinkMovementMethod.getInstance());

        updateSelectedPage(vfFlipper, view);

        // ------------------
        // Step 0 - Info Page
        // ------------------

        view.findViewById(R.id.btAgree).setOnClickListener(v -> {
            if (!isAgeValid(v.getContext())) {
                return;
            }
            mSelectedConsent = GDPRConsent.PERSONAL_CONSENT;
            mCurrentStep = 1;
            updateSelectedPage(vfFlipper, view);
        });

        view.findViewById(R.id.btDisagree).setOnClickListener(v -> {
            if (!isAgeValid(v.getContext())) {
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
            updateSelectedPage(vfFlipper, view);
        });

        if (!mSetup.allowNoConsent()) {
            btNoConsentAtAll.setVisibility(View.GONE);
        } else {
            btNoConsentAtAll.setOnClickListener(v -> {
                mSelectedConsent = GDPRConsent.NO_CONSENT;
                mCurrentStep = 3;
                updateSelectedPage(vfFlipper, view);
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

    private boolean isAgeValid(Context context) {
        if (mSetup.askForAgeConfirmation()) {
            if (!mAgeConfirmed) {
                Toast.makeText(context, R.string.gdpr_age_not_confirmed, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void updateSelectedPage(ViewFlipper vfFlipper, View view) {
        vfFlipper.setDisplayedChild(mCurrentStep);
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
