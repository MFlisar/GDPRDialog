### GDPRDialog [![Release](https://jitpack.io/v/MFlisar/GDPRDialog.svg)](https://jitpack.io/#MFlisar/GDPRDialog)

Simple reusable `DialogFragment`.

![GDPR Demo1](https://github.com/MFlisar/GDPRDialog/blob/master/demo/demo1.gif "demo1")
![GDPR Demo2](https://github.com/MFlisar/GDPRDialog/blob/master/demo/demo2.gif "demo2")

### What it offers

This library offers following:

* supports `DialogFragment` or `BottomSheetDialogFragment` style 
* supports multiple services, already defined ones are AdMob and Firebase
* supports custom service definitions
* is set up via a setup class that allows you to select which possibilities you give the user - any combination of *personalised ads*, *non personalised ads* and *paid version*, depending on what you want. Examples:
  * allow *personalised ads* or *paid version* only
  * allow *personalised ads*, *non personalised ads* or *paid or free version*
  * combine whatever you want here...
* optionally enable google's check if user is requesting consent form within the EAA (be careful when using this)
* optionally adds a `Checkbox` for age confirmation
* uses soft opt in by default if you offer e.g. a *personalised ads* vs *non personalised ads* version
* it closes the app if the user did not give any consent (i.e if the user clicks the back button in the dialog)
* it manages the user's selected consent decision and remembers it
* it automatically reshows the dialog if the user did not give any consent or if the setup defines that the app is not allowed to be used without ads and the user has not accepted ads at all yet

### GDPR and law safety

Such dialogs must always be adjusted to the use case in general, although this one should be fine in most cases. 

Checkout following to find out more:

* Some infos I wrote together: [My GDPR ReadMe](README-GDPR.md)
* Check out this page for more: [EU GDPR](https://www.eugdpr.org/)

*Just to make this clear, I'm no lawyer, so don't take anything I write for granted! It should be fine, but there is no guarantee.*

### Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the compile statement to your module's `build.gradle`:
```groovy
dependencies {
     implementation 'com.github.MFlisar:GDPRDialog:0.5.3'
}
```

### Usage

1. Init the singleton in your application
```groovy
GDPR.getInstance().init(this);
```
2. call following in your activities `onCreate`
```groovy
GDPRSetup setup = new GDPRSetup(GDPRDefinitions.ADMOB); // add all networks you use to the constructor, signature is `GDPRSetup(GDPRNetwork... adNetworks)`
setup.withPrivacyPolicy("www.my-privacy-policy.com");   // provide your own privacy policy, optional but very recommended
// optionally change setup:
// setup.withAllowNoConsent(true);
// setup.withPaidVersion(allowNonPersonalisedOptionAsWell);
// setup.withExplicitAgeConfirmation(true);
// setup.withCheckRequestLocation(true);
// setup.withBottomSheet(true);
// setup.withForceSelection(true);
// setup.withCustomDialogTheme(theme);
GDPR.getInstance().checkIfNeedsToBeShown(this /* extends AppCompatActivity & GDPR.IGDPRCallback */, setup);
```
3. implement the `GDPR.IGDPRCallback` in your activity
```groovy
public class ExampleActivity extends AppCompatActivity implements GDPR.IGDPRCallback {
    @Override
    public void onConsentInfoUpdate(GDPRConsent consentState, boolean isNewState) {
        // handle consent here
    }
	
    @Override
    public void onConsentNeedsToBeRequested() {
        // we need to get consent, so we show the dialog here
        GDPR.getInstance().showDialog(this, mSetup);
    }
}
```

Check out the [demo](https://github.com/MFlisar/GDPRDialog/blob/master/app/src/main/java/com/michaelflisar/gdprdialog/demo/DemoActivity.java) for a full working example

### TODO

* [ ] translations
  * [x] english
  * [x] german
  * [ ] others