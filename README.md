# DEPRECATED

This library is deprecated in favour of my [MaterialDialogs](https://github.com/MFlisar/MaterialDialogs) library - it's a very modularised library so switching to it won't add a lot of unnecessary overload if you don't want it.

**Switching is easy, because I've based the GDPR Dialog module completely on this library, so after switching to new library the new library will reuse the settings from this library automatically (it will reuse any already given consent)**

Read the readme(s) over there if you want a full insight, but here's a short example how it works after switching:

**Side Note:** after switching, you do not need to implement any interface in an activity anymore - the dialogs use a more convenient alternative solution.

**Usage:**

### 1) create a setup
```kotlin
// nearly a kotlin based copy of the GDPRSetup from this library
val setup = GDPRSetup(
    networks = listOf(GDPRDefinitions.ADMOB),
    policyLink = "https://www.policy.com",
    //explicitAgeConfirmation = true,
    //hasPaidVersion = true,
    //allowNoConsent = true,
    explicitNonPersonalisedConfirmation = true
    // ...
)
```

### 2) ask for consent if necessary

```kotlin
val shouldAskForConsent = GDPR.shouldAskForConsent(activity, setup)
L.d { "currentConsent = $currentConsent | shouldAskForConsent = $shouldAskForConsent" }
// we always show the dialog in this demo so we comment out the if!
if (shouldAskForConsent) {
    DialogGDPR(
    	1100, // some id for identification
    	setup = setup
    )
        .showDialogFragment(activity)
}
```

### 3) handle the dialog result

The ID parameter is optional, you can listen to ALL events of a specific type as well, but check out the other library for more details.

```kotlin
// handle ALL events
onMaterialDialogEvent<DialogGDPR.Event>(1100) { event ->
    // handle the event including cancels...
}
// handle 
onMaterialDialogEvent<DialogGDPR.Event.Result>(1100) { event ->
    // handle successful events only...
	val state = event.consent
}
```

### 4) query consent if needed
```kotlin
val currentConsent = GDPR.getCurrentConsentState(this, setup)
val canCollectPersonalInformation = GDPR.canCollectPersonalInformation(this, setup)
```

# README

Check out the old readme of this repository here: [README](README-ORIGINAL.md)