package com.michaelflisar.gdprdialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelflisar.gdprdialog.helper.GDPRViewManager;

public abstract class GDPRActivity extends AppCompatActivity implements GDPR.IGDPRCallback
{
    private GDPRViewManager mViewManager;

    public static <T extends  GDPRActivity> void startActivityForResult(Activity activity, GDPRSetup setup, Class<T> clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        intent.putExtra(GDPRViewManager.ARG_SETUP, setup);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewManager = new GDPRViewManager(getIntent().getExtras(), savedInstanceState);
        mViewManager.setCallback(this);

        View view = initView(LayoutInflater.from(this), null);
        setContentView(view);

        getSupportActionBar().setTitle(R.string.gdpr_dialog_title);
    }

    @Override
    public void onDestroy() {
        onSaveConsentAndCloseActivity();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mViewManager.getSetup().forceSelection() && mViewManager.getSelectedConsent() == null) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewManager.save(outState);
    }

    @Override
    public void onConsentNeedsToBeRequested() {
        // ignored in activity, must be checked befroe starting the activity!
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.gdpr_dialog, container, false);
        mViewManager.init(this, view, () -> onSaveConsentAndCloseActivity());
        return view;
    }

    private void onSaveConsentAndCloseActivity() {
        if (mViewManager.shouldCloseApp()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                ActivityCompat.finishAffinity(this);
            }
        } else {
            finish();
        }
        mViewManager.reset();
    }
}