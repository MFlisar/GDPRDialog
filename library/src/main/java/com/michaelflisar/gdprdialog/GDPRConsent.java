package com.michaelflisar.gdprdialog;

public enum GDPRConsent {
    UNKNOWN,
    NO_CONSENT,
    NON_PERSONAL_CONSENT_ONLY,
    PERSONAL_CONSENT,
    AUTOMATIC_PERSONAL_CONSENT;

    public boolean isPersonalConsent() {
        return this == PERSONAL_CONSENT || this == AUTOMATIC_PERSONAL_CONSENT;
    }
}