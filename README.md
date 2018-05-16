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

### Gradle (via [JitPack.io](https://jitpack.io/))

**For now, simply use `compile 'com.github.MFlisar:GDPRDialog:-SNAPSHOT'`, I'll make a release reliably on 24.05, one day before the the new GDPR is valid.**

1. add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the compile statement to your module's `build.gradle`:
```groovy
dependencies {
     implementation 'com.github.MFlisar:GDPRDialog:-SNAPSHOT'
}
```

### Usage

1. Init the singleton in your application
```groovy
GDPR.getInstance().init(this);
```
2. call following in your activities `onCreate`
```groovy
GDPR.getInstance().showIfNecessary(this, new GDPRSetup(GDPR.ADMOB_NETWORK));
```
3. implement the `GDPR.IGDPRActivity` in your activity
```groovy
public class ExampleActivity extends AppCompatActivity implements GDPR.IGDPRActivity {
    @Override
    public void onConsentInfoUpdate(GDPRConsent consentState, boolean isNewState) {
        // handle consent here
    }
}
```

Check out the [demo](https://github.com/MFlisar/GDPRDialog/blob/master/app/src/main/java/com/michaelflisar/gdprdialog/demo/MainActivity.java) for a full working example

### TODO

* [ ] resize dialog after each step
* [ ] translations => german will be done by my until the first release

Additional things todo:

* maybe improve texts
* ad more network strings, currently only AdMob is existing
* more???