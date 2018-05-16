package com.michaelflisar.gdprdialog;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

public class GDPRDialog extends AppCompatDialogFragment
{
    private static String ARG_SETUP = "ARG_SETUP";

    private static String KEY_STEP = "KEY_STEP";
    private static String KEY_SELECTED_CONSENT = "KEY_SELECTED_CONSENT";

    public static GDPRDialog newInstance(GDPRSetup setup) {
        GDPRDialog dlg = new GDPRDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SETUP, setup);
        dlg.setArguments(args);
        return dlg;
    }

    private GDPRSetup mSetup = null;
    private GDPR.IGDPRActivity mCallback = null;

    private int mCurrentStep = 0;
    private GDPRConsent mSelectedConsent = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mCallback = (GDPR.IGDPRActivity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent activity must implement GDPR.IGDPRActivity interface!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSetup = getArguments().getParcelable(ARG_SETUP);

        if (savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getInt(KEY_STEP);
            if (savedInstanceState.containsKey(KEY_SELECTED_CONSENT)) {
                mSelectedConsent = GDPRConsent.values()[savedInstanceState.getInt(KEY_SELECTED_CONSENT)];
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        onSaveConsentAndCloseDialog();
        super.onDismiss(dialogInterface);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_STEP, mCurrentStep);
        if (mSelectedConsent != null) {
            outState.putInt(KEY_SELECTED_CONSENT, mSelectedConsent.ordinal());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater, container);
        getDialog().setTitle(R.string.gdpr_dialog_title);
        return view;
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.gdpr_dialog, container, false);

        final ViewFlipper vfFlipper = view.findViewById(R.id.vfFlipper);
        final Button btNoAdsPlease = view.findViewById(R.id.btNoAdsPlease);
        final TextView tvText = view.findViewById(R.id.tvText);
        final TextView tvTextNonPersonalAccepted = view.findViewById(R.id.tvTextNonPersonalAccepted);
        final TextView tvTextPersonalAccepted = view.findViewById(R.id.tvTextPersonalAccepted);
        final TextView tvTextNothingAccepted = view.findViewById(R.id.tvTextNothingAccepted);
        final TextView tvAdsInfo = view.findViewById(R.id.tvAdsInfo);
        tvText.setText(Html.fromHtml(inflater.getContext().getString(R.string.gdpr_dialog_text, mSetup.getNetworksCommaSeperated(inflater.getContext(), true))));
        tvTextNonPersonalAccepted.setText(Html.fromHtml(inflater.getContext().getString(R.string.gdpr_dialog_text_after_accepted_non_personal, mSetup.getNetworksCommaSeperated(inflater.getContext(),false))));
        tvTextPersonalAccepted.setText(Html.fromHtml(inflater.getContext().getString(R.string.gdpr_dialog_text_after_accepted_personal)));
        tvTextNothingAccepted.setText(Html.fromHtml(inflater.getContext().getString(R.string.gdpr_dialog_text_after_accepted_nothing)));

        if (!mSetup.containsAdNetwork()) {
            tvAdsInfo.setVisibility(View.GONE);
        }

        tvText.setMovementMethod(LinkMovementMethod.getInstance());
        tvTextNonPersonalAccepted.setMovementMethod(LinkMovementMethod.getInstance());

        updateSelectedPage(vfFlipper, view);

        // ------------------
        // Step 0 - Info Page
        // ------------------

        view.findViewById(R.id.btAgree).setOnClickListener(v -> {
            mSelectedConsent = GDPRConsent.PERSONAL_CONSENT;
            mCurrentStep = 1;
            updateSelectedPage(vfFlipper, view);
        });

        view.findViewById(R.id.btDisagree).setOnClickListener(v -> {
            mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
            mCurrentStep = 2;
            updateSelectedPage(vfFlipper, view);
        });

        if (!mSetup.isAllowUsageWithoutConsent()) {
            btNoAdsPlease.setVisibility(View.GONE);
        } else {
            btNoAdsPlease.setOnClickListener(v -> {
                mSelectedConsent = GDPRConsent.NO_CONSENT;
                mCurrentStep = 3;
                updateSelectedPage(vfFlipper, view);
            });
        }

        // ------------------
        // Step 1 - User accepted personal ads page
        // ------------------

        view.findViewById(R.id.btCloseAccepted).setOnClickListener(v -> onSaveConsentAndCloseDialog());

        // ------------------
        // Step 2 - User did not accept personal ads page
        // ------------------

        view.findViewById(R.id.btCloseAcceptedNonPersonal).setOnClickListener(v -> {
            onSaveConsentAndCloseDialog();
        });

        // ------------------
        // Step 3 - User did not accept any ads page
        // ------------------

        view.findViewById(R.id.btCloseNoAds).setOnClickListener(v -> {
            onSaveConsentAndCloseDialog();
        });

        return view;
    }

    private void updateSelectedPage(ViewFlipper vfFlipper, View view) {
        vfFlipper.setDisplayedChild(mCurrentStep);
        // TODO: resize dialog...
        // view.requestLayout();
    }

    private void onSaveConsentAndCloseDialog() {
        if (mSelectedConsent != null) {
            GDPR.getInstance().setConsent(mSelectedConsent);
            mCallback.onConsentInfoUpdate(mSelectedConsent, true);
        }
        if (mSelectedConsent == null || (mSelectedConsent == GDPRConsent.NO_CONSENT && !mSetup.isAllowUsageWithoutConsent())) {
            if (getActivity() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().finishAndRemoveTask();
                } else {
                    getActivity().finishAffinity();
                }
            }
        } else {
            dismiss();
        }
    }
}
