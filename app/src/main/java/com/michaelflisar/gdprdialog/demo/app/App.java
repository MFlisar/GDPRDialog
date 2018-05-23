package com.michaelflisar.gdprdialog.demo.app;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;

import com.michaelflisar.gdprdialog.GDPR;

import java.util.Locale;

public class App extends Application
{
    // simply global flag, don't do this in a production app!!
    public static boolean USE_ACTIVITY = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // following should be done ONCE only in the application
        GDPR.getInstance().init(this);
    }
}
