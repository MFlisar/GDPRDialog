package com.michaelflisar.gdprdialog.demo;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.michaelflisar.gdprdialog.GDPRCustomTexts;
import com.michaelflisar.gdprdialog.GDPRDefinitions;
import com.michaelflisar.gdprdialog.GDPRLocationCheck;
import com.michaelflisar.gdprdialog.GDPRNetwork;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.GDPRSubNetwork;
import com.michaelflisar.gdprdialog.demo.app.App;
import com.michaelflisar.gdprdialog.demo.databinding.ActivitySetupBinding;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySetupBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void showPredefinedDialog() {
        GDPRSetup setup = null;
        String policyLink = "www.test.com";
        int selectedIndex = mBinding.rgSelection.indexOfChild(mBinding.rgSelection.findViewById(mBinding.rgSelection.getCheckedRadioButtonId()));
        // not always necessary, but here this avoids that the global definition is changed if sub networks are loaded from AdMob
        GDPRNetwork admob = GDPRDefinitions.ADMOB.copy();
        switch (selectedIndex) {
            case 0:
                setup = new GDPRSetup(admob)
                        .withPrivacyPolicy(policyLink)
                        .withPaidVersion(false);
                break;
            case 1:
                setup = new GDPRSetup(admob)
                        .withPrivacyPolicy(policyLink)
                        .withPaidVersion(false)
                        .withCheckRequestLocation(GDPRLocationCheck.DEFAULT);
                break;
            case 2:
                String publisherId = mBinding.etPublisherIdCommon.getText().toString();
                if (publisherId.length() == 0) {
                    Snackbar.make(mBinding.getRoot(), "Please enter a publisher ID before trying this setup", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                setup = new GDPRSetup(admob)
                        .withPrivacyPolicy(policyLink)
                        .withPaidVersion(false)
                        .withCheckRequestLocation(GDPRLocationCheck.DEFAULT)
                        .withLoadAdMobNetworks(publisherId);
                break;
            case 3:
                // EditText
                break;
            case 4:
                setup = new GDPRSetup(admob)
                        .withCheckRequestLocation(GDPRLocationCheck.DEFAULT);
                break;
            case 5:
                setup = new GDPRSetup(admob)
                        .withCheckRequestLocation(GDPRLocationCheck.DEFAULT)
                        .withAllowNoConsent(true);
                break;
            case 6:
                setup = new GDPRSetup(admob)
                        .withCheckRequestLocation(GDPRLocationCheck.DEFAULT)
                        .withPaidVersion(true);
                break;
            case 7:
                Intent intent = new Intent(this, MinimalDemoActivity.class);
                startActivity(intent);
                return;
        }

        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("setup", setup);
        startActivity(intent);
    }

    private void showCustomDialog() {
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
            List<GDPRLocationCheck> checks = new ArrayList<>();
            checks.add(GDPRLocationCheck.INTERNET);
            if (mBinding.cbCheckRequestLocationFallbackTelephonyManager.isChecked()) {
                checks.add(GDPRLocationCheck.TELEPHONY_MANAGER);
            }
            if (mBinding.cbCheckRequestLocationFallbackTimeZone.isChecked()) {
                checks.add(GDPRLocationCheck.TIMEZONE);
            }
            setup
                    .withCheckRequestLocation(checks.toArray(new GDPRLocationCheck[checks.size()]));
        }
        if (mBinding.cbLoadAdMobProviders.isChecked()) {
            setup.withLoadAdMobNetworks(mBinding.etPublisherId.getText().toString());
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
        // our base theme has a toolbar, so wo do not need this
        // setup.withNoToolbarTheme(true);

        GDPRCustomTexts customTexts = new GDPRCustomTexts();
        if (mBinding.etCustomTitle.getText().length() > 0) {
            customTexts.withTitle(mBinding.etCustomTitle.getText().toString());
        }
        if (mBinding.etCustomQuestion.getText().length() > 0) {
            customTexts.withQuestion(mBinding.etCustomQuestion.getText().toString());
        }
        if (mBinding.etCustomMainMsg.getText().length() > 0) {
            customTexts.withMainText(mBinding.etCustomMainMsg.getText().toString());
        }
        if (mBinding.etCustomTopMsg.getText().length() > 0) {
            customTexts.withTopText(mBinding.etCustomTopMsg.getText().toString());
        }
        if (mBinding.etCustomAgeMsg.getText().length() > 0) {
            customTexts.withAgeMsg(mBinding.etCustomAgeMsg.getText().toString());
        }

        // this call is optional, if you do not use any custom texts this function can be skipped
        setup.withCustomTexts(customTexts);

        intent.putExtra("setup", setup);
        startActivity(intent);
    }

    // --------------------------------
    // Layout and layout event handling
    // --------------------------------

    @Override
    protected void onDestroy() {
        mBinding.unbind();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (mBinding.viewpager.getCurrentItem() == 0) {
            showPredefinedDialog();
        } else {
            showCustomDialog();
        }
    }

    private void initViews() {
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

        ViewPagerAdapter adapter = new ViewPagerAdapter();
        mBinding.viewpager.setAdapter(adapter);
        mBinding.tabs.setupWithViewPager(mBinding.viewpager);
    }

    class ViewPagerAdapter extends PagerAdapter {
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.sv1;
                    break;
                case 1:
                    resId = R.id.sv2;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Common use cases";
                case 1:
                    return "Custom builder";
            }
            return super.getPageTitle(position);
        }
    }
}
