package ir.vahidhoseini.gmc.android.RetrofitResult;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;
import okhttp3.OkHttpClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ir.vahidhoseini.gmc.android.BuildConfig;
import ir.vahidhoseini.gmc.android.R;

public class NetworkClient {
    public Retrofit retrofit;
    public Activity currentActivity;

    public NetworkClient(Activity currentActivity) {
        this.currentActivity = currentActivity;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.app_management)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public void setControlApp() {
        AppManagerApi managerApi = retrofit.create(AppManagerApi.class);
        Call<ManagingApps> call = managerApi.getManagmentData();
        call.enqueue(new Callback<ManagingApps>() {
            @Override
            public void onResponse(Call<ManagingApps> call, Response<ManagingApps> response) {
                if (response.body() != null) {
                    ManagingApps managingApps = response.body();
                    List<ManagingApps.update> app_update_list = managingApps.getAppUpdate();
                    for (int i = 0; i < app_update_list.size(); i++) {
                        if (BuildConfig.APPLICATION_ID.equals(app_update_list.get(i).getApp_id())) {
                            if (BuildConfig.VERSION_CODE < app_update_list.get(i).version_code()) {
                                showNotifUpdate(app_update_list.get(i).getTitle(),
                                        app_update_list.get(i).getDesc(),
                                        app_update_list.get(i).getUpdateable(),
                                        app_update_list.get(i).getUrl_download());
                            }
                        }
                    }

                    boolean app_can_show_ex_ads = false;
                    boolean app_can_show_my_random_ads = false;

                    List<ManagingApps.ListAds> ListAds = managingApps.getListAds();
                    for (int i = 0; i < ListAds.size(); i++) { //
                        if (BuildConfig.APPLICATION_ID.equals(ListAds.get(i).get_id_app())) {
                            if (!isAppInstalled(ListAds.get(i).get_id_app_ads()) && ListAds.get(i).get_id_app_ads().contains(".")) {
                                app_can_show_ex_ads = false;
                                app_can_show_my_random_ads = false;
//                                Log.e("tag", "app_management value a:" + specific_ads_for_specific_apps + random_ads);
                                if (ListAds.get(i).get_activating_my_ads()) {
                                    showMyAds(ListAds.get(i).get_title(),
                                            ListAds.get(i).get_color_title(),
                                            ListAds.get(i).get_url_photo(),
                                            ListAds.get(i).get_color_btn(),
                                            ListAds.get(i).get_url_action(),
                                            ListAds.get(i).get_text_url_action());
                                } else if (ListAds.get(i).get_activating_ex_ads()) {
                                    showExAds();
                                }
                                break;
                            } else {
                                app_can_show_ex_ads = ListAds.get(i).get_activating_ex_ads();
                                app_can_show_my_random_ads = ListAds.get(i).get_activating_my_ads();
                                ListAds.remove(i);

                                break;
                            }
                        } else {
                            app_can_show_my_random_ads = true;
                        }
                    }
                    if (app_can_show_my_random_ads) {
                        for (int j = ListAds.size() - 1; j >= 0; j--) {
                            if (isAppInstalled(ListAds.get(j).get_id_app_ads())) {
                                ListAds.remove(j);
                            }
                        }
                        if (ListAds.size() > 0) {
                            int randomAds = new Random().nextInt(ListAds.size());//select random of my ads
                            showMyAds(ListAds.get(randomAds).get_title(),
                                    ListAds.get(randomAds).get_color_title(),
                                    ListAds.get(randomAds).get_url_photo(),
                                    ListAds.get(randomAds).get_color_btn(),
                                    ListAds.get(randomAds).get_url_action(),
                                    ListAds.get(randomAds).get_text_url_action());
                        } else {
                            if (app_can_show_ex_ads) {
                                showExAds();
                            }
                        }
                    } else {
                        if (app_can_show_ex_ads) {
                            showExAds();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("tag", "app_management data: " + t.toString());
                showExAds();
            }
        });

    }

    private String adId;

    private void requestAd() {
        Tapsell.requestAd(currentActivity, BuildConfig.TAPSELL_INTERSTITIAL_BANNER, new TapsellAdRequestOptions(), new TapsellAdRequestListener() {
            @Override
            public void onAdAvailable(String adId) {
                NetworkClient.this.adId = adId;
                showAd();

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void showAd() {
        Tapsell.showAd(currentActivity,
                BuildConfig.TAPSELL_INTERSTITIAL_BANNER,
                adId,
                new TapsellShowOptions(),
                new TapsellAdShowListener() {
                    @Override
                    public void onOpened() {
                    }

                    @Override
                    public void onClosed() {
                    }

                    @Override
                    public void onError(String message) {
                    }

                });
    }

    public void showExAds() {
        requestAd();

    }

    private void showMyAds(String gettitle, String getColorTitle, String geturl_photo, String getColorBtn, final String geturl_action, String gettext_url_action) {
        final Dialog dialog = new Dialog(currentActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_ads);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        TextView txt_title = dialog.findViewById(R.id.ads_title);
        TextView ads_btn = dialog.findViewById(R.id.ads_btn);
        ImageView ads_background = dialog.findViewById(R.id.ads_background);
        ImageView ads_close = dialog.findViewById(R.id.ads_close);
        txt_title.setText(gettitle);
        txt_title.setTextColor(Color.parseColor(getColorTitle));
        ads_btn.setText(gettext_url_action);
        ads_btn.setBackgroundColor(Color.parseColor(getColorBtn));
        ads_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geturl_action));
                currentActivity.startActivity(browserIntent);
            }
        });
        ads_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        Picasso.with(currentActivity).load(geturl_photo).into(ads_background, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                dialog.show();
            }

            @Override
            public void onError() {

            }
        });


    }

    private void showNotifUpdate(String title, String desc, boolean updateable, final String url_download) {
        if (updateable) {
            final Dialog dialog = new Dialog(currentActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_app_update);
            dialog.setCancelable(true);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            TextView txt_title = dialog.findViewById(R.id.txt_title);
            TextView txt_desc = dialog.findViewById(R.id.txt_desc);
            TextView btn_netgativ = dialog.findViewById(R.id.btn_netgativ);
            TextView btn_positive = dialog.findViewById(R.id.btn_positive);
            txt_title.setText(title);
            txt_desc.setText(desc);
            btn_netgativ.setText("IGNORE");
            btn_positive.setText("UPDATE");
            btn_netgativ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btn_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_download));
                    currentActivity.startActivity(browserIntent);
                }
            });
        }
    }

    public boolean isAppInstalled(String package_name) {
        if (BuildConfig.APPLICATION_ID.equals(package_name)) {
            return true;
        }

        try {
            PackageManager pm = currentActivity.getPackageManager();
            PackageInfo info = pm.getPackageInfo("" + package_name, PackageManager.GET_META_DATA);
            return true;

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}