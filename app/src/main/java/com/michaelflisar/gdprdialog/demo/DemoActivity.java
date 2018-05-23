package com.michaelflisar.gdprdialog.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.demo.app.App;
import com.michaelflisar.gdprdialog.demo.gdpr.DemoGDPRActivity;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener, GDPR.IGDPRCallback
{
    private final int DEMO_GDPR_ACTIVITY_REQUEST_CODE = 123;

    // Setup
    private GDPRSetup mSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // get setup from intent
        mSetup = getIntent().getParcelableExtra("setup");

        // init state texts
        GDPRConsent consent = GDPR.getInstance().getConsent();
        GDPRLocation location = GDPR.getInstance().getRequestLocation();
        if (consent != null) {
            ((TextView) findViewById(R.id.tvCurrentConsent)).setText(consent.name());
        }
        if (location != null) {
            ((TextView) findViewById(R.id.tvCurrentLocation)).setText(location.name());
        }

        // show GDPR Dialog if necessary, the library takes care about if and how to show it
        showGDPRIfNecessary();
    }

    @Override
    public void onClick(View v) {
        GDPR.getInstance().resetConsent();
        // reshow dialog instantly
        showGDPRIfNecessary();
    }

    private void showGDPRIfNecessary() {
        GDPR.getInstance().checkIfNeedsToBeShown(this, mSetup);
    }

    // --------------------
    //  GDPR.IGDPRCallback
    // --------------------

    @Override
    public void onConsentNeedsToBeRequested(GDPRLocation location) {
        if (App.USE_ACTIVITY) {
            DemoGDPRActivity.startActivityForResult(this, mSetup, DemoGDPRActivity.class, DEMO_GDPR_ACTIVITY_REQUEST_CODE);
        } else {
            // default: forward the result and show the dialog
            GDPR.getInstance().showDialog(this, mSetup, location);
        }

        ((TextView) findViewById(R.id.tvCurrentLocation)).setText(location.name());
    }

    @Override
    public void onConsentInfoUpdate(GDPRConsent consentState, boolean isNewState) {
        if (isNewState) {
            // user just selected this consent, do whatever you want...
            switch (consentState) {
                case UNKNOWN:
                    // never happens!
                    break;
                case NO_CONSENT:
                    Toast.makeText(this, "User does NOT accept ANY ads - depending on your setup he may want to buy the app though, handle this!", Toast.LENGTH_LONG).show();
                    break;
                case NON_PERSONAL_CONSENT_ONLY:
                    Toast.makeText(this, "User accepts NON PERSONAL ads", Toast.LENGTH_LONG).show();
                    onConsentKnown(consentState == GDPRConsent.PERSONAL_CONSENT);
                    break;
                case PERSONAL_CONSENT:
                    Toast.makeText(this, "User accepts PERSONAL ads", Toast.LENGTH_LONG).show();
                    onConsentKnown(consentState == GDPRConsent.PERSONAL_CONSENT);
                    break;
            }
        } else {
            switch (consentState) {
                case UNKNOWN:
                    // never happens!
                    break;
                case NO_CONSENT:
                    // with the default setup, the dialog will shown in this case again anyways!
                    break;
                case NON_PERSONAL_CONSENT_ONLY:
                case PERSONAL_CONSENT:
                    // user restarted activity and consent was already given...
                    onConsentKnown(consentState == GDPRConsent.PERSONAL_CONSENT);
                    break;
            }
        }

        ((TextView) findViewById(R.id.tvCurrentConsent)).setText(consentState.name());
    }

    private void onConsentKnown(boolean allowsPersonalAds) {
        // TODO:
        // init your ads based on allowsPersonalAds
    }

    // --------------------
    // Only necessaqry for the activity demo!!!
    // --------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DEMO_GDPR_ACTIVITY_REQUEST_CODE) {
            GDPRConsent consentState = GDPR.getInstance().getConsent();
            ((TextView) findViewById(R.id.tvCurrentConsent)).setText(consentState != null ? consentState.name() : "");
        }
    }
}
