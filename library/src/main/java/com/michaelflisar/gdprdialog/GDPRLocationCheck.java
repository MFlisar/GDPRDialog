package com.michaelflisar.gdprdialog;

public enum GDPRLocationCheck {
    /**
     * use this to check the location via the same url google's consent sdk uses
     */
    INTERNET,
    /**
     * use this to check the location via the TelephonyManager
     */
    TELEPHONY_MANAGER,
    /**
     * use this to check the location via the TimeZone
     */
    TIMEZONE,
    /**
     * use this to check the location via the locale
     */
    LOCALE;

    public static GDPRLocationCheck[] DEFAULT = {
            INTERNET
    };

    public static GDPRLocationCheck[] DEFAULT_WITH_FALLBACKS = {
            INTERNET,
            TELEPHONY_MANAGER,
            TIMEZONE,
            LOCALE
    };
}
