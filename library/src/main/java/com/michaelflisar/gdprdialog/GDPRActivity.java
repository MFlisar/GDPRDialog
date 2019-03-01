package com.michaelflisar.gdprdialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelflisar.gdprdialog.helper.GDPRPreperationData;
import com.michaelflisar.gdprdialog.helper.GDPRViewManager;

public abstract class GDPRActivity extends AppCompatActivity implements GDPR.IGDPRCallback
{
    private GDPRViewManager mViewManager;

    public static <T extends  GDPRActivity> void startActivityForResult(Activity activity, GDPRSetup setup, GDPRLocation location, Class<T> clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        intent.replaceExtras(GDPRViewManager.createBundle(setup, location));
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
        if (mViewManager.handleBackPress()) {
            return;
        }
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
    public void onConsentNeedsToBeRequested(GDPRPreperationData data) {
        // ignored in activity, must be checked before starting the activity!
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