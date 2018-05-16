package com.michaelflisar.gdprdialog;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        dlg.setCancelable(false);
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
        ((TextView )view.findViewById(R.id.tvText)).setText(inflater.getContext().getString(R.string.gdpr_dialog_text, mSetup.getNetworksCommaSeperated()));
        ((TextView )view.findViewById(R.id.tvTextPersonalDeclined)).setText(inflater.getContext().getString(R.string.gdpr_dialog_text_after_declined_personal, mSetup.getNetworksCommaSeperated()));

        vfFlipper.setDisplayedChild(mCurrentStep);

        // ------------------
        // Step 0 - Info Page
        // ------------------

        view.findViewById(R.id.btAgree).setOnClickListener(v -> {
            mSelectedConsent = GDPRConsent.PERSONAL_CONSENT;
            mCurrentStep = 1;
            vfFlipper.setDisplayedChild(mCurrentStep);
        });

        view.findViewById(R.id.btDisagree).setOnClickListener(v -> {
            mCurrentStep = 2;
            vfFlipper.setDisplayedChild(mCurrentStep);
        });

        // ------------------
        // Step 1 - User accepted personal ads page
        // ------------------

        view.findViewById(R.id.btCloseAccepted).setOnClickListener(v -> onSaveConsentAndCloseDialog());

        // ------------------
        // Step 2 - User did NOT accept personal ads page
        // ------------------

        view.findViewById(R.id.btAgreeNonPersonal).setOnClickListener(v -> {
            mSelectedConsent = GDPRConsent.NON_PERSONAL_CONSENT_ONLY;
            mCurrentStep = 3;
            vfFlipper.setDisplayedChild(mCurrentStep);
        });

        view.findViewById(R.id.btDisagree2).setOnClickListener(v -> {
            mSelectedConsent = GDPRConsent.NO_CONSENT;
            mCurrentStep = 4;
            vfFlipper.setDisplayedChild(mCurrentStep);
        });

        // ------------------
        // Step 3 - User did accept NON personal ads page
        // ------------------

        view.findViewById(R.id.btCloseAcceptedNonPersonal).setOnClickListener(v -> {
            onSaveConsentAndCloseDialog();
        });

        // ------------------
        // Step 4 - User DIDN'T ACCEPT ANYTHING ads page
        // ------------------

        view.findViewById(R.id.btCloseAcceptedNothing).setOnClickListener(v -> {
            onSaveConsentAndCloseDialog();
        });


        return view;
    }

    private void onSaveConsentAndCloseDialog() {
        GDPR.getInstance().setConsent(mSelectedConsent);
        mCallback.onConsentInfoUpdate(mSelectedConsent, true);
        if (mSelectedConsent != null && mSelectedConsent == GDPRConsent.NO_CONSENT && !mSetup.isAllowUsageWithoutConsent()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().finishAndRemoveTask();
            } else {
                getActivity().finishAffinity();
            }
        } else {
            dismiss();
        }
    }
}
