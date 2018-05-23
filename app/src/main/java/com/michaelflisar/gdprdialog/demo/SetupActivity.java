package com.michaelflisar.gdprdialog.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import com.michaelflisar.gdprdialog.GDPRDefinitions;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.demo.app.App;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener
{
    private CheckBox cbShowAsActivity;
    private CheckBox cbAdServiceOnly;
    private CheckBox cbAllowNoConsent;
    private CheckBox cbHasPaidVersion;
    private CheckBox cbAllowNonPersonalisedForPaidVersions;
    private CheckBox cbAskForAge;
    private CheckBox cbCheckRequestLocation;
    private CheckBox cbBottomSheet;
    private CheckBox cbForceSelection;
    private CheckBox cbAskNonPersonalised;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        cbShowAsActivity = findViewById(R.id.cbShowAsActivity);
        cbAdServiceOnly = findViewById(R.id.cbAdServiceOnly);
        cbAllowNoConsent = findViewById(R.id.cbAllowNoConsent);
        cbHasPaidVersion = findViewById(R.id.cbHasPaidVersion);
        cbAllowNonPersonalisedForPaidVersions = findViewById(R.id.cbAllowNonPersonalisedForPaidVersions);
        cbAskForAge = findViewById(R.id.cbAskForAge);
        cbCheckRequestLocation = findViewById(R.id.cbCheckRequestLocation);
        cbBottomSheet = findViewById(R.id.cbBottomSheet);
        cbForceSelection = findViewById(R.id.cbForceSelection);
        cbAskNonPersonalised = findViewById(R.id.cbAskNonPersonalised);

        cbAllowNonPersonalisedForPaidVersions.setEnabled(cbHasPaidVersion.isChecked());
        cbHasPaidVersion.setOnCheckedChangeListener((buttonView, isChecked) -> cbAllowNonPersonalisedForPaidVersions.setEnabled(isChecked));
        cbShowAsActivity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            App.USE_ACTIVITY = isChecked;
            cbBottomSheet.setEnabled(!isChecked);
        });

        cbShowAsActivity.setChecked(App.USE_ACTIVITY);
        cbBottomSheet.setEnabled(!App.USE_ACTIVITY);
    }

    @Override
    protected void onDestroy() {
        cbShowAsActivity = null;
        cbAdServiceOnly = null;
        cbAllowNoConsent = null;
        cbHasPaidVersion = null;
        cbAllowNonPersonalisedForPaidVersions = null;
        cbAskForAge = null;
        cbCheckRequestLocation = null;
        cbBottomSheet = null;
        cbForceSelection = null;
        cbAskNonPersonalised = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(this, DemoActivity.class);

        // Setup GDPRSetup and pass it to the activity
        String policyLink = "www.test.com"; // provide your apps policy link
        GDPRSetup setup;
        if (cbAdServiceOnly.isChecked()) {
            setup = new GDPRSetup(GDPRDefinitions.ADMOB);
        } else {
            setup = new GDPRSetup(GDPRDefinitions.ADMOB, GDPRDefinitions.FIREBASE_DATABASE, GDPRDefinitions.FIREBASE_CRASH);
        }
        // you should always provide your own policy as well...
        setup.withPrivacyPolicy(policyLink);

        // following is all optional, default behaviour is to allow personalised or non personalised data only with no paid version
        if (cbAllowNoConsent.isChecked()) {
            setup.withAllowNoConsent(true);
        }
        if (cbHasPaidVersion.isChecked()) {
            setup.withPaidVersion(cbAllowNonPersonalisedForPaidVersions.isChecked());
        }
        if (cbAskForAge.isChecked()) {
            setup.withExplicitAgeConfirmation(true);
        }
        if (cbCheckRequestLocation.isChecked()) {
            setup.withCheckRequestLocation(true);
        }
        if (cbBottomSheet.isChecked()) {
            setup.withBottomSheet(true);
        }
        if (cbForceSelection.isChecked()) {
            setup.withForceSelection(true);
        }
        if (cbAskNonPersonalised.isChecked()) {
            setup.withExplicitNonPersonalisedConfirmation(true);
        }
        // our base theme has a toolbar, so wo do not need this
        // setup.withNoToolbarTheme(true);
        intent.putExtra("setup", setup);
        startActivity(intent);
    }
}
