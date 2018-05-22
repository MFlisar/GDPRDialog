### GDPRDialog [![Release](https://jitpack.io/v/MFlisar/GDPRDialog.svg)](https://jitpack.io/#MFlisar/GDPRDialog)

Simple reusable `DialogFragment`.

![GDPR Workflow](https://github.com/MFlisar/GDPRDialog/blob/master/screenshots/demo1.gif "demo1")
![GDPR Workflow](https://github.com/MFlisar/GDPRDialog/blob/master/screenshots/demo2.gif "demo2")

### Workflow

![GDPR Workflow](https://github.com/MFlisar/GDPRDialog/blob/master/screenshots/workflow.png "Workflow")

### What it offers

This library offers following:

* it manages the user's selected consent decision
* it closes the app if the user did not give any consent (i.e if the user clicks the back button in the dialog)
* it automatically reshows the dialog if the user did not give any consent or if the setup defines that the app is not allowed to be used without ads and the user has not accepted ads at all yet
* works with a dialog fragment, so rotation and remembering intermediate state is done in the library
* uses soft opt in
* optionally you can check user's request lcoation via google's method and only show the dialog to users within the EAA (imho, you should be cautious with this)
* allows setups like following:
  * allow/disallow app usage if the user gives no consent
  * optioanlly explicitly ask for the users age (it's saver according to a data security expert I know)
  * allow to handle fallback to paid app instead of not personalised ads and also allows to offer personalised/non personalised/paid no ads version
  
### GDPR and law safety

Such dialogs must always be adjusted to the user case in general, although this one should be fine in most cases. Following is important (talked with a lawyer I know of who is a GDPR specialist). I'm no lawyer, so don't take everything I write for granted. Check out the the [EU GDPR](https://www.eugdpr.org/) yourself for more informations.

To be on the safe side, I can give following advices:

* show the dialog to everyone, not only to people within the EAA (especially if you from within the EAA yourself)

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
     implementation 'com.github.MFlisar:GDPRDialog:0.1'
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
// setup.withAskForAge(true);
// setup.withCheckRequestLocation(true);
GDPR.getInstance().showIfNecessary(this, setup);
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

* [ ] resize dialog after each step
* [ ] translations
  * [x] english
  * [x] german
  * [ ] other
* [ ] offer bottom dialog layout as well
* add more networks (dropbox, ...)