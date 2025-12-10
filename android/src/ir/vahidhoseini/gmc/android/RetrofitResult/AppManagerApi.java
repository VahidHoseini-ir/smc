package ir.vahidhoseini.gmc.android.RetrofitResult;

import retrofit2.Call;
import retrofit2.http.GET;
import ir.vahidhoseini.gmc.android.BuildConfig;

public interface AppManagerApi {
    /*
    Get request to fetch city weather.Takes in two parameter-city name and API key.
    */
//    + BuildConfig.app_management
    @GET("management/" + BuildConfig.app_management_file)
    Call<ManagingApps> getManagmentData();
}