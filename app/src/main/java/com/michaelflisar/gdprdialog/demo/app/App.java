package com.michaelflisar.gdprdialog.demo.app;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

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
        GDPR.getInstance()
                .init(this)
                .initLogger(new GDPR.ILogger() {
                    @Override
                    public void debug(String tag, String msg) {
                        Log.d(String.format("GDPRDemo [%s]", tag), msg);
                    }

                    @Override
                    public void error(String tag, String msg, Throwable tr) {
                        Log.e(String.format("GDPRDemo [%s]", tag), msg, tr);
                    }
                });
    }
}
