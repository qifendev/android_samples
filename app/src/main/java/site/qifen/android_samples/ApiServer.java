package site.qifen.android_samples;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServer {


    @POST("/dsp")
    Call<Result<Data>> waterMark(@Query("key")String key,@Query("token")String token,@Query("url") String url);

}
