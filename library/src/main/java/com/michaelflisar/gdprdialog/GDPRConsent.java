package com.michaelflisar.gdprdialog;

import android.content.Context;

import com.michaelflisar.gdprdialog.helper.GDPRUtils;

import java.util.Date;

public enum GDPRConsent {
    UNKNOWN,
    NO_CONSENT,
    NON_PERSONAL_CONSENT_ONLY,
    PERSONAL_CONSENT;
}