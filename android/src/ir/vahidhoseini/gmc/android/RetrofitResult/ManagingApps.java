package ir.vahidhoseini.gmc.android.RetrofitResult;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ManagingApps {
    @SerializedName("Update")
    public List<update> appupdate;
    @SerializedName("ListAds")
    public List<ListAds> listAds;


    public ManagingApps(List<update> appupdate, List<ListAds> listAds) {
        this.appupdate = appupdate;
        this.listAds = listAds;
    }

    public List<update> getAppUpdate() {
        return appupdate;
    }


    public class update {
        @SerializedName("app_id")
        public String app_id;
        @SerializedName("updateable")
        public boolean updateable;
        @SerializedName("title")
        public String title;
        @SerializedName("desc")
        public String desc;
        @SerializedName("url_download")
        public String url_download;
        @SerializedName("version_code")
        public int version_code;

        public update(String app_id, boolean updateable, String title, String desc, String url_download, int version_code) {
            this.app_id = app_id;
            this.updateable = updateable;
            this.title = title;
            this.desc = desc;
            this.url_download = url_download;
            this.version_code = version_code;
        }

        public String getApp_id() {
            return app_id;
        }

        public boolean getUpdateable() {
            return updateable;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }

        public String getUrl_download() {
            return url_download;
        }

        public int version_code() {
            return version_code;
        }
    }

    public List<ListAds> getListAds() {
        return listAds;
    }


    public class ListAds {

        @SerializedName("id_app")
        public String id_app;
        @SerializedName("id_app_ads")
        public String id_app_ads;
        @SerializedName("activating_ex_ads")
        public boolean activating_ex_ads;
        @SerializedName("activating_my_ads")
        public boolean activating_my_ads;
        @SerializedName("title")
        public String title;
        @SerializedName("color_title")
        public String color_title;
        @SerializedName("url_photo")
        public String url_photo;
        @SerializedName("color_btn")
        public String color_btn;
        @SerializedName("url_action")
        public String url_action;
        @SerializedName("text_url_action")
        public String text_url_action;

        public ListAds(String id_app, String id_app_ads, boolean activating_ex_ads, boolean activating_my_ads , String title, String color_title, String url_photo, String color_btn, String url_action, String text_url_action) {
            this.id_app = id_app;
            this.id_app_ads = id_app_ads;
            this.activating_ex_ads = activating_ex_ads;
            this.activating_my_ads = activating_my_ads;
            this.title = title;
            this.color_title = color_title;
            this.url_photo = url_photo;
            this.color_btn = color_btn;
            this.url_action = url_action;
            this.text_url_action = text_url_action;
        }

        public String get_id_app() {
            return id_app;
        }
        public String get_id_app_ads() {
            return id_app_ads;
        }
        public boolean get_activating_ex_ads() {
            return activating_ex_ads;
        }
        public boolean get_activating_my_ads() {
            return activating_my_ads;
        }
        public String get_title() {
            return title;
        }
        public String get_color_title() {
            return color_title;
        }
        public String get_url_photo() {
            return url_photo;
        }
        public String get_color_btn() {
            return color_btn;
        }
        public String get_url_action() {
            return url_action;
        }
        public String get_text_url_action() {
            return text_url_action;
        }
    }


}
