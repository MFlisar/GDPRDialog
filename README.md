### GDPRDialog [![Release](https://jitpack.io/v/MFlisar/GDPRDialog.svg)](https://jitpack.io/#MFlisar/GDPRDialog)

Simple reusable `DialogFragment`.

![GDPR Workflow](https://github.com/MFlisar/GDPRDialog/blob/master/screenshots/demo1.gif "demo1")
![GDPR Workflow](https://github.com/MFlisar/GDPRDialog/blob/master/screenshots/demo2.gif "demo2")

### Workflow

![GDPR Workflow](https://github.com/MFlisar/GDPRDialog/blob/master/screenshots/workflow.png "Workflow")

### What it offers

This library offers following:

* supports multiple services, already defined ones are AdMob and Firebase
* supports custom service definitions
* is set up via a setup class that allows you to select which possibilities you give the user - any combination of *personalised ads*, *non personalised ads* and *paid version*, depending on what you want. Examples:
  * allow *personalised ads* or *paid version* only
  * allow *personalised ads*, *non personalised ads* or *paid or free version*
  * combine whatever you want here...
* optionally enable google's check if user is requesting consent form within the EAA (be careful when using this)
* optionally adds a `Spinner` for age selection
* uses soft opt in by default if you offer e.g. a *personalised ads* vs *non personalised ads* version
* it closes the app if the user did not give any consent (i.e if the user clicks the back button in the dialog)
* it manages the user's selected consent decision and remembers it
* it automatically reshows the dialog if the user did not give any consent or if the setup defines that the app is not allowed to be used without ads and the user has not accepted ads at all yet

### GDPR and law safety

Such dialogs must always be adjusted to the user case in general, although this one should be fine in most cases. Following is important (talked with a lawyer I know of who is a GDPR specialist). I'm no lawyer, so don't take everything I write for granted. Check out the the [EU GDPR](https://www.eugdpr.org/) yourself for more informations.

To be on the safe side, I can give following advices:

* show the dialog to everyone, not only to people within the EAA (especially if you from within the EAA yourself)
* ask the user for the age explicitly (decide yourself here, I will do it implicitly but explicitly would be the way the industry is doing it in real life)

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
     implementation 'com.github.MFlisar:GDPRDialog:0.4'
}
```

### Usage

1. Init the singleton in your application
```groovy
GDPR.getInstance().init(this);
```
2. call following in your activities `onCreate`
```groovy
GDPRSetup setup = new GDPRSetup(GDPR.ADMOB_NETWORK); // add all networks you use to the constructor
// optionally change setup:
// setup.withAllowNoConsent(true);
// setup.withPaidVersion(allowNonPersonalisedOptionAsWell);
// setup.withExplicitAgeConfirmation(true);
// setup.withCheckRequestLocation(true);
// setup.withExplicitConsentForEachService(true)
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

* [ ] better landscape layout
* [ ] translations
  * [x] english
  * [x] german
  * [ ] others
* [ ] offer bottom dialog layout as well
* [ ] if `withExplicitConsentForEachService` is used, the user currently needs to accept every service => could be improved to define some as optional
* add more networks (dropbox, ...)