### GDPRDialog [![Release](https://jitpack.io/v/MFlisar/GDPRDialog.svg)](https://jitpack.io/#MFlisar/GDPRDialog) ![Localisation](https://img.shields.io/badge/Localisation-10%2F24%20(42%25)-blue.svg)

Simple reusable `DialogFragment`.

![GDPR Demo1](https://github.com/MFlisar/GDPRDialog/blob/master/demo/demo1.gif "demo1")
![GDPR Demo2](https://github.com/MFlisar/GDPRDialog/blob/master/demo/demo2.gif "demo2")

### What it offers

This library offers following:

* supports `DialogFragment` or `BottomSheetDialogFragment` style 
* supports multiple services, already defined ones are AdMob and Firebase
* supports intermediator services as well, also supports to load your ad providers from AdMob
* supports custom service definitions
* is set up via a setup class that allows you to select which possibilities you give the user - any combination of *personalised ads*, *non personalised ads* and *paid version*, depending on what you want. Examples:
  * allow *personalised ads* or *paid version* only
  * allow *personalised ads*, *non personalised ads* or *paid or free version*
  * combine whatever you want here...
* optionally enable location checks (supports google's check from the SDK via the internet, `TelephoneManager`, `TimeZone`, `Locale`) and also allows to define to use fallback methods, by providing your own list of checks sorted by their priority
* optionally adds a `Checkbox` for age confirmation
* uses soft opt in by default if you offer e.g. a *personalised ads* vs *non personalised ads* version
* it closes the app if the user did not give any consent (i.e if the user clicks the back button in the dialog)
* it manages the user's selected consent decision and remembers it (including location, date and app version)
* it automatically reshows the dialog if the user did not give any consent or if the setup defines that the app is not allowed to be used without ads and the user has not accepted ads at all yet

### GDPR and law safety

Such dialogs must always be adjusted to the use case in general, although this one should be fine in most cases. 

Checkout following to find out more: [EU GDPR](https://www.eugdpr.org/)

*Just to make this clear, I'm no lawyer and I can't guarantee that you are save if you use this library.*

### Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:
```java
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the compile statement to your module's `build.gradle`:
```java
dependencies {
     implementation 'com.github.MFlisar:GDPRDialog:LATEST-VERSION'
}
```

LATEST-VERSION: [![Release](https://jitpack.io/v/MFlisar/GDPRDialog.svg)](https://jitpack.io/#MFlisar/GDPRDialog)

### Usage

1. Init the singleton in your application
```java
GDPR.getInstance().init(this);
```
2. call following in your activities `onCreate`
```java
GDPRSetup setup = new GDPRSetup(GDPRDefinitions.ADMOB) // add all networks you use to the constructor, signature is `GDPRSetup(GDPRNetwork... adNetworks)`
    // everything is optional, but you should at least provide your policy
    .withPrivacyPolicy("www.my-privacy-policy.com")
    .withAllowNoConsent(true)
    .withPaidVersion(allowNonPersonalisedOptionAsWell)
    .withExplicitAgeConfirmation(true)
    .withCheckRequestLocation(GDPRLocationCheck.DEFAULT) // pass in an array of location check methods, predefined arrays like `DEFAULT` and `DEFAULT_WITH_FALLBACKS` do exists
    .withCheckRequestLocationTimeouts(readTimeout, connectTimeout)
    .withBottomSheet(true)
    .withForceSelection(true)
    .withCustomDialogTheme(theme)
    .withShortQuestion(true)
    .withLoadAdMobNetworks(publisherId(s)) // e.g. "pub-0123456789012345"
    .withNoToolbarTheme(noToolbarTheme) // true, if you use a theme without a toolbar, false otherwise
    .withShowPaidOrFreeInfoText(true) // show the info that this app is cheap/free based on the networks or hide it
    .withCustomTexts(customTexts) // provide custom texts (title, top message, main message, question text, age confirmation text) by resource or string
;
GDPR.getInstance().checkIfNeedsToBeShown(this /* extends AppCompatActivity & GDPR.IGDPRCallback */, setup);
```
3. implement the `GDPR.IGDPRCallback` in your activity
```java
public class ExampleActivity extends AppCompatActivity implements GDPR.IGDPRCallback {
    @Override
    public void onConsentInfoUpdate(GDPRConsentState consentState, boolean isNewState) {
        // handle consent here
		
    }
	
    @Override
    public void onConsentNeedsToBeRequested() {
        // we need to get consent, so we show the dialog here
        GDPR.getInstance().showDialog(this, mSetup);
    }
}
```
4. Other usages
```java
// get current consent anywhere in the app after user has given consent
GDPRConsentState consentState = GDPR.getInstance().getConsentState();
// get location, time, app version of given consent
GDPRConsent consent = consentState.getConsent(); // the given constent
GDPRLocation location = consentState.getLocation(); // where has the given consent been given
long date = consentState.getDate(); // when has the given consent been given
int appVersion = consentState.getVersion(); // in which app version has the consent been given
// check if you can use personal informations or not
boolean canCollectPersonalInformation = GDPR.getInstance().canCollectPersonalInformation();
```

Check out the [MinimalDemo](https://github.com/MFlisar/GDPRDialog/blob/master/app/src/main/java/com/michaelflisar/gdprdialog/demo/MinimalDemoActivity.java) to get something to start with or check out the [DemoActivity](https://github.com/MFlisar/GDPRDialog/blob/master/app/src/main/java/com/michaelflisar/gdprdialog/demo/DemoActivity.java) with the [example setups](https://github.com/MFlisar/GDPRDialog/blob/master/app/src/main/java/com/michaelflisar/gdprdialog/demo/SetupActivity.java) for a more complex example

### Where can I add additional networks?

You can simply do this in the [GDPRDefinitions](https://github.com/MFlisar/GDPRDialog/blob/master/library/src/main/java/com/michaelflisar/gdprdialog/GDPRDefinitions.java). Of course you can always define new networks in project only as well, but if you think you use a service many others do use as well, fell free to add it to the definitions.

### Migration

Migrations will be explained in the [release notes](https://github.com/MFlisar/GDPRDialog/releases)

### TODO

* [ ] Localisation ![Localisation](https://img.shields.io/badge/Localisation-10%2F24%20(42%25)-blue.svg)
  
  At least translations for all official languages within the european union should be added
  * [ ] Bulgarian
  * [ ] Croatian
  * [x] Czech
  * [ ] Danish
  * [ ] Dutch
  * [x] English
  * [ ] Estonian
  * [ ] Finnish
  * [x] French
  * [x] German
  * [ ] Greek
  * [ ] Hungarian
  * [ ] Irish
  * [x] Italian
  * [ ] Latvian
  * [ ] Lithuanian
  * [ ] Maltese
  * [x] Polish
  * [x] Portuguese
  * [ ] Romanian
  * [x] Slovak
  * [ ] Slovenian
  * [x] Spanish
  * [x] Swedish
  
### License

[Apache2](/LICENSE)
