package com.michaelflisar.gdprdialog.demo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.michaelflisar.gdprdialog.GDPRDefinitions;
import com.michaelflisar.gdprdialog.GDPRNetwork;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.GDPRSubNetwork;
import com.michaelflisar.gdprdialog.demo.app.App;
import com.michaelflisar.gdprdialog.demo.databinding.ActivitySetupBinding;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySetupBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setup);

        mBinding.cbAllowNonPersonalisedForPaidVersions.setEnabled(mBinding.cbHasPaidVersion.isChecked());
        mBinding.cbHasPaidVersion.setOnCheckedChangeListener((buttonView, isChecked) -> mBinding.cbAllowNonPersonalisedForPaidVersions.setEnabled(isChecked));
        mBinding.cbShowAsActivity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            App.USE_ACTIVITY = isChecked;
            mBinding.cbBottomSheet.setEnabled(!isChecked);
        });
        mBinding.cbCheckRequestLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mBinding.cbCheckRequestLocationFallbackTelephonyManager.setEnabled(isChecked);
            mBinding.cbCheckRequestLocationFallbackTimeZone.setEnabled(isChecked);
        });

        mBinding.cbShowAsActivity.setChecked(App.USE_ACTIVITY);
        mBinding.cbBottomSheet.setEnabled(!App.USE_ACTIVITY);
    }

    @Override
    protected void onDestroy() {
        mBinding.unbind();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, DemoActivity.class);

        // Setup GDPRSetup and pass it to the activity
        String policyLink = "www.test.com"; // provide your apps policy link
        GDPRSetup setup;

        GDPRNetwork admobNetwork = GDPRDefinitions.ADMOB;
        if (mBinding.cbAddSomeSubNetworksToAdMob.isChecked()) {
            // like this you can add some sub networks for services that are intermediators;
            // e.g. add all your ad providers from AdMob that you use
            // for testing I add the same ad provider 10 times, there is no limit here!
            admobNetwork = GDPRDefinitions.ADMOB.copy()
                    .addSubNetwork(new GDPRSubNetwork("Google1", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google2", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google3", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google4", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google5", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google6", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google7", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google8", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google9", "https://policies.google.com/technologies/partner-sites"))
                    .addSubNetwork(new GDPRSubNetwork("Google10", "https://policies.google.com/technologies/partner-sites"))
                    ;
        }

        if (mBinding.cbAdServiceOnly.isChecked()) {
            setup = new GDPRSetup(admobNetwork);
        } else {
            setup = new GDPRSetup(
                    admobNetwork,
                    GDPRDefinitions.FIREBASE_DATABASE,
                    GDPRDefinitions.FIREBASE_CRASH,
                    GDPRDefinitions.FIREBASE_ANALYTICS,
                    GDPRDefinitions.MOPUB);
        }
        // you should always provide your own policy as well...
        setup.withPrivacyPolicy(policyLink);

        // following is all optional, default behaviour is to allow personalised or non personalised data only with no paid version
        if (mBinding.cbAllowNoConsent.isChecked()) {
            setup.withAllowNoConsent(true);
        }
        if (mBinding.cbHasPaidVersion.isChecked()) {
            setup.withPaidVersion(mBinding.cbAllowNonPersonalisedForPaidVersions.isChecked());
        }
        if (mBinding.cbAskForAge.isChecked()) {
            setup.withExplicitAgeConfirmation(true);
        }
        if (mBinding.cbCheckRequestLocation.isChecked()) {
            setup.withCheckRequestLocation(
                    true,
                    mBinding.cbCheckRequestLocationFallbackTelephonyManager.isChecked(),
                    mBinding.cbCheckRequestLocationFallbackTimeZone.isChecked());
        }
        if (mBinding.cbBottomSheet.isChecked()) {
            setup.withBottomSheet(true);
        }
        if (mBinding.cbForceSelection.isChecked()) {
            setup.withForceSelection(true);
        }
        if (mBinding.cbAskNonPersonalised.isChecked()) {
            setup.withExplicitNonPersonalisedConfirmation(true);
        }
        if (mBinding.cbShowServicesAsList.isChecked()) {
            setup.withShowNetworksAsList(true);
        }
        // our base theme has a toolbar, so wo do not need this
        // setup.withNoToolbarTheme(true);
        intent.putExtra("setup", setup);
        startActivity(intent);
    }
}
