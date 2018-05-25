package com.michaelflisar.gdprdialog;

public enum GDPRConsent {
    /**
     * users consent is unknown, it needs to be requests
     */
    UNKNOWN,
    /**
     * user consent given: he does not accept any usage of personal data
     */
    NO_CONSENT,
    /**
     * user consent given: he accept non personal data only
     */
    NON_PERSONAL_CONSENT_ONLY,
    /**
     * user consent given: he accepts personal data usage
     */
    PERSONAL_CONSENT,
    /**
     * user consent automatically set because of request location: personal data usage is possible
     */
    AUTOMATIC_PERSONAL_CONSENT;

    public boolean isPersonalConsent() {
        return this == PERSONAL_CONSENT || this == AUTOMATIC_PERSONAL_CONSENT;
    }
}