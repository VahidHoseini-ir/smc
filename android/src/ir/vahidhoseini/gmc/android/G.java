package ir.vahidhoseini.gmc.android;

import android.app.Application;


import com.onesignal.OneSignal;

import ir.tapsell.sdk.Tapsell;

public class G  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tapsell.initialize(this, BuildConfig.TAPSELL_KEY);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

    }
}
