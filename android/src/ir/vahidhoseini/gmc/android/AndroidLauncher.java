package ir.vahidhoseini.gmc.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.pushpole.sdk.PushPole;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;
import ir.vahidhoseini.gmc.AdHandler;
import ir.vahidhoseini.gmc.AdResult;
import ir.vahidhoseini.gmc.MaryoGame;
import ir.vahidhoseini.gmc.PrefHandler;
import ir.vahidhoseini.gmc.android.RetrofitResult.NetworkClient;
import ir.vahidhoseini.gmc.screen.GameScreen;
import ir.vahidhoseini.gmc.utility.GameSave;

import static android.graphics.Typeface.BOLD;


public class AndroidLauncher extends AndroidApplication implements MaryoGame.Event, AdHandler, PrefHandler {

    private SharedPreferences.Editor preferencesEditor;
    private SharedPreferences sharedPreferences;
    private String sharedpreffile = BuildConfig.APPLICATION_ID;

    private final String DATE_KEY = "uDateOfUsed";
    private final String REWARD_KEY = "uRewardCount";
    private final String COUNT_PLAYING_GAME_KEY = "todayPlayingGame";
    private final String LAST_PLAYING_GAME_KEY = "lastPlayingGame";
    private SimpleDateFormat sdf;
    private Calendar calendar;
    private TextView timerView;
    private RelativeLayout layout;

    private final int HOW_MANY_VIDEO_REWARD = 2;
    private final int HOW_MANY_TIME_PLAY_A_DAY = 3;
    private final int SHOW_ADS = 1;
    private final int HID_ADS = 0;
    private int RewardCount;
    private int PlayedCount;
    public static Activity CTXActivity;

    private void requestAd() {
        RewardCount = sharedPreferences.getInt(REWARD_KEY, 1);
        if (RewardCount <= HOW_MANY_VIDEO_REWARD) {
            final TapsellShowOptions tapsellAdRequestOptions = new TapsellShowOptions();
            tapsellAdRequestOptions.setBackDisabled(true);
            Tapsell.requestAd(AndroidLauncher.this,
                    BuildConfig.TAPSELL_REWARDED_Video,
                    new TapsellAdRequestOptions(),
                    new TapsellAdRequestListener() {
                        @Override
                        public void onAdAvailable(String adId) {
                            Tapsell.showAd(AndroidLauncher.this,
                                    BuildConfig.TAPSELL_REWARDED_Video,
                                    adId,
                                    tapsellAdRequestOptions,
                                    new TapsellAdShowListener() {
                                        @Override
                                        public void onOpened() {
                                        }

                                        @Override
                                        public void onClosed() {
                                            Log.e("TAG message", "message : " + "ad is closed");
                                            AndroidLauncher.this.postRunnable(new Runnable() {
                                                @Override
                                                public void run() {
                                                    gameScreen.proceedFromPausedOrEnded();
                                                    adResult.GetAdResult("ONLINE");
                                                }
                                            });

                                        }

                                        @Override
                                        public void onError(String message) {
                                            Log.e("TAG message", "message : " + message);
                                        }

                                        @Override
                                        public void onRewarded(boolean completed) {
                                            preferencesEditor.putString(DATE_KEY, sdf.format(calendar.getTime()));
                                            RewardCount++;
                                            preferencesEditor.putInt(REWARD_KEY, RewardCount);
                                            preferencesEditor.commit();
                                            GameSave.addLifes(1);
                                        }
                                    });

                        }

                        @Override
                        public void onError(String message) {
                        }
                    });

        } else if (CanUseTodayReward()) {
            preferencesEditor.putInt(REWARD_KEY, 1);
            preferencesEditor.commit();
            requestAd();
        } else {
            adResult.GetAdResult("NoReward");
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_ADS:
                    Log.e("TAG", "screen is toched ");
                    requestAd();
                    break;
                case HID_ADS:
                    requestAd();
                    break;

            }
        }
    };


    public boolean CanUseTodayReward() {
        String getUsedRewardTime = sharedPreferences.getString(DATE_KEY, sdf.format(calendar.getTime()));
        try {
            Date strDate = sdf.parse(getUsedRewardTime);
            Date date = new Date();

            /// HERE i make the reward if change the date and becom to reall date can have rewards just by this part { ||date.getDay() < strDate.getDay()) }
            if (date.getDay() > strDate.getDay()) {
                return true;
            } else if (date.getMonth() > strDate.getMonth()) {
                return true;
            } else if (date.getYear() > strDate.getYear()) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CTXActivity = this;
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        sharedPreferences = getSharedPreferences(sharedpreffile, MODE_PRIVATE);
        preferencesEditor = sharedPreferences.edit();

        layout = new RelativeLayout(this);
        timerView = new TextView(this);


        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = true;
        config.hideStatusBar = true;
        config.useImmersiveMode = true;
        config.useGLSurfaceView20API18 = true;
        MaryoGame game = new MaryoGame(this, this, this);
        View gameView = initializeForView(game, config);

        layout.addView(gameView);

        RelativeLayout.LayoutParams timerViewParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        timerViewParams.setMargins(18, 18, 22, 18);
        timerViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, gameView.getId());
        timerView.setTypeface(null, BOLD);
        timerView.setTextSize(18);
        timerView.setPadding(18, 18, 22, 18);
        timerView.setTextColor(Color.RED);
        timerView.setLayoutParams(timerViewParams);
        layout.addView(timerView);


        setContentView(layout);


        game.isRunningOn = Build.MODEL;
        input.addKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == Input.Keys.MEDIA_FAST_FORWARD) {
                    input.onKey(v, Input.Keys.SPACE, event);
                    return true;
                }
                return false;
            }
        });

        PushPole.initialize(this, true);
        new NetworkClient(this).setControlApp();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void levelStart(String levelName) {
    }

    @Override
    public void levelEnd(String levelName, boolean success) {
    }

    private GameScreen gameScreen;
    private AdResult adResult;

    @Override
    public void ShowAds(boolean show, GameScreen gameScreen, AdResult result) {
        this.gameScreen = gameScreen;
        this.adResult = result;
        if (isOnline()) {
            handler.sendEmptyMessage(show ? SHOW_ADS : HID_ADS);
        } else {
            adResult.GetAdResult("OFFLINE");
        }
    }

    public boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sockaddr, timeoutMs);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void startTimer() {
        new CountDownTimer(gapTime, 1000) {
            public void onTick(long millisUntilFinished) {
                int secend = (int) (millisUntilFinished / 1000);
                int minuets = secend / 60;
                timerView.setText(String.format("%02d", minuets) + " : " + String.format("%02d", secend % 60));
            }

            public void onFinish() {
                timerView.setText("");
                preferencesEditor.putInt(COUNT_PLAYING_GAME_KEY, 0);
                preferencesEditor.commit();

            }
        }.start();
    }

    private int gapTime = 600000;
    //    private int gapTime = 100;
    Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case show_timer: {
                    Log.e("TAG", "play is clicked");
                    startTimer();
                    break;
                }
                case hide_timer: {
                    Log.e("TAG", "hide_timer");
                    timerView.setVisibility(View.GONE);
                    break;
                }
                case visible_timer: {
                    Log.e("TAG", "hide_timer");
                    timerView.setVisibility(View.VISIBLE);
                    break;
                }
                case buzz_timer: {
                    Log.e("TAG", "buzz_timer");
                    animation = AnimationUtils.loadAnimation(AndroidLauncher.this, R.anim.buzz);
                    timerView.setAnimation(animation);
                    break;
                }

            }
        }
    };

    boolean dev_mode = false;
    Animation animation;
    private final int show_timer = 0;
    private final int hide_timer = 1;
    private final int visible_timer = 2;
    private final int buzz_timer = 3;

    @Override
    public boolean setTimeFor(String clicked) {
        switch (clicked) {
            case "BTN_PLAY_CLICKED": {
                PlayedCount = sharedPreferences.getInt(COUNT_PLAYING_GAME_KEY, 0);
                Log.e("VAHID:BTN_PLAY_CLICKED", "PlayedCount :" + PlayedCount);
                if (PlayedCount < HOW_MANY_TIME_PLAY_A_DAY || dev_mode) {
                    timeHandler.sendEmptyMessage(hide_timer);
                    return true;
                } else {
                    timeHandler.sendEmptyMessage(buzz_timer);
                    return false;
                }
            }
            case "ChosedLevelToPlay": {
                PlayedCount++;
                preferencesEditor.putInt(COUNT_PLAYING_GAME_KEY, PlayedCount);
                preferencesEditor.commit();
                PlayedCount = sharedPreferences.getInt(COUNT_PLAYING_GAME_KEY, 0);
                Log.e("VAHID:ChosedLevelToPlay", "PlayedCount :" + PlayedCount);
                long time = System.currentTimeMillis();
                preferencesEditor.putLong(LAST_PLAYING_GAME_KEY, time);
                preferencesEditor.commit();
            }
            case "ShowTimer": {
                PlayedCount = sharedPreferences.getInt(COUNT_PLAYING_GAME_KEY, 0);
                Long passedTime = sharedPreferences.getLong(LAST_PLAYING_GAME_KEY, 0);
                passedTime = System.currentTimeMillis() - passedTime;
                Log.e("VAHID:passedTime", "passedTime :" + passedTime);
                if (0 < passedTime && passedTime < gapTime) {
//                    if(!timer_is_start){
                    Log.e("VAHID:ShowTimer", "PlayedCount :" + PlayedCount);
                    if (PlayedCount >= HOW_MANY_TIME_PLAY_A_DAY) {
                        gapTime = (int) (gapTime - passedTime);
                        timeHandler.sendEmptyMessage(show_timer);
                    }
//                    }
                } else {
                    preferencesEditor.putInt(COUNT_PLAYING_GAME_KEY, 0);
                    preferencesEditor.commit();
                }

            }
            case "hide_timer": {
                timeHandler.sendEmptyMessage(hide_timer);

            }
            case "visible_timer": {
                timeHandler.sendEmptyMessage(visible_timer);

            }

        }

        return false;
    }


}
