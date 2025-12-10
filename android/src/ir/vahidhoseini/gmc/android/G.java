package ir.vahidhoseini.gmc.android;

import android.app.Application;


import com.onesignal.OneSignal;

import ir.tapsell.sdk.Tapsell;

public class G  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tapsell.initialize(this, BuildConfig.TAPSELL_KEY);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.WARN, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(BuildConfig.ONE_SIGNAL_APP_ID);

    }
}
