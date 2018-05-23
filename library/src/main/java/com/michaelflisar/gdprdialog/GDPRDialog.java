package com.michaelflisar.gdprdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.michaelflisar.gdprdialog.helper.GDPRViewManager;

import java.util.ArrayList;

public class GDPRDialog extends AppCompatDialogFragment
{
    private GDPRViewManager mViewManager;

    public static GDPRDialog newInstance(GDPRSetup setup) {
        GDPRDialog dlg = new GDPRDialog();
        Bundle args = new Bundle();
        args.putParcelable(GDPRViewManager.ARG_SETUP, setup);
        dlg.setArguments(args);
        return dlg;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewManager.setCallback(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewManager = new GDPRViewManager(getArguments(), savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (mViewManager.handleBackPress())
            return;
        onSaveConsentAndCloseDialog();
        super.onDismiss(dialogInterface);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewManager.save(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView(inflater, container);
        if (!mViewManager.getSetup().noToolbarTheme()) {
            getDialog().setTitle(R.string.gdpr_dialog_title);
        }
        return view;
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.gdpr_dialog, container, false);
        mViewManager.init(getActivity(), view, () -> onSaveConsentAndCloseDialog());
        return view;
    }

    private void onSaveConsentAndCloseDialog() {
        if (mViewManager.shouldCloseApp()) {
            if (getActivity() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().finishAndRemoveTask();
                } else {
                    ActivityCompat.finishAffinity(getActivity());
                }
            }
        } else {
            dismiss();
        }
        mViewManager.reset();
    }
}