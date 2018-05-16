### GDPRDialog [![Release](https://jitpack.io/v/MFlisar/GDPRDialog.svg)](https://jitpack.io/#MFlisar/GDPRDialog)

Simple reusable `DialogFragment`. Texts are based on 
https://media.mopub.com/media/filer_public/3c/fa/3cfa8de2-e517-4b27-ad83-d997d6c0ceab/flow3_v3.png 
and are a little adjusted and extended.

![GDPR Workflow](https://github.com/MFlisar/GDPRDialog/blob/master/screenshots/workflow.png "Workflow")

**For now, simply use `compile 'com.github.MFlisar:GDPRDialog:anyBranch-SNAPSHOT'`, I'll make a release reliably on 24.05, one day before the the new GDPR is valid.**

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
     implementation 'com.github.MFlisar:GDPRDialog:-SNAPSHOT'
}
```

### Usage

1. Init the singleton in your application
```groovy
GDPR.getInstance().init(this);
```
2. call following in your activities `onCreate`:
```groovy
GDPR.getInstance().showIfNecessary(this, new GDPRSetup(this, R.string.gdpr_network_admob));
```
3. implement the `GDPR.IGDPRActivity` in your activity

Check out the demo for a full working example