package com.michaelflisar.gdprdialog.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsent;
import com.michaelflisar.gdprdialog.GDPRSetup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GDPR.IGDPRActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // following should be done ONCE only in the application,
        // for demo purposes we do it here
        GDPR.getInstance().init(this);

        // show GDPR Dialog if necessary, the library takes fully care about if and how to show it
        GDPR.getInstance().showIfNecessary(this,
                new GDPRSetup(GDPR.ADMOB_NETWORK)
                // use this to allow to use the app without any consent as well
//                    .withAllowUsageWithoutConsent(true)
        );
    }

    @Override
    public void onConsentInfoUpdate(GDPRConsent consentState, boolean isNewState) {
        // TODO...
        if (isNewState) {
            // user just selected this consent, do whatever you want...
            switch (consentState) {
                case UNKNOWN:
                    // never happens!
                    break;
                case NO_CONSENT:
                    Toast.makeText(this, "User does NOT accept ANY ads, app is closed by the library!", Toast.LENGTH_LONG).show();
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

        ((TextView)findViewById(R.id.tvCurrentConsent)).setText(consentState.name());
    }

    private void onConsentKnown(boolean allowsPersonalAds) {
        // TODO:
        // init your ads based on allowsPersonalAds
    }

    @Override
    public void onClick(View v) {
        GDPR.getInstance().resetConsent();
        // reshow dialog instantly
        GDPR.getInstance().showIfNecessary(this, new GDPRSetup(GDPR.ADMOB_NETWORK));
    }
}
