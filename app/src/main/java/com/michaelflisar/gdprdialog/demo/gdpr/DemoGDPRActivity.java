package com.michaelflisar.gdprdialog.demo.gdpr;

import android.widget.Toast;

import com.michaelflisar.gdprdialog.GDPRActivity;
import com.michaelflisar.gdprdialog.GDPRConsent;

public class DemoGDPRActivity extends GDPRActivity
{
    @Override
    public void onConsentInfoUpdate(GDPRConsent consentState, boolean isNewState)
    {
        if (isNewState)
        {
            // user just selected this consent, do whatever you want...
            switch (consentState)
            {
                case UNKNOWN:
                    // never happens!
                    break;
                case NO_CONSENT:
                    Toast.makeText(this, "User does NOT accept ANY ads - depending on oyur setup he may want to buy the app though, handle this!", Toast.LENGTH_LONG).show();
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
        }
        else
        {
            switch (consentState)
            {
                case UNKNOWN:
                    // never happens!
                    break;
                case NO_CONSENT:
                    // with the default setup, the dialog will be shown again in this case anyways!
                    break;
                case NON_PERSONAL_CONSENT_ONLY:
                case PERSONAL_CONSENT:
                    // user restarted activity and consent was already given...
                    onConsentKnown(consentState == GDPRConsent.PERSONAL_CONSENT);
                    break;
            }
        }
    }

    private void onConsentKnown(boolean allowsPersonalAds)
    {
        // TODO:
        // init your ads based on allowsPersonalAds
    }
}
