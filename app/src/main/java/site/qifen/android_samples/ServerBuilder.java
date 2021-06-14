package site.qifen.android_samples;

import com.google.gson.GsonBuilder;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * url操作服务
 */

public class ServerBuilder {


    /**
     * http请求固定逻辑
     */


   static final  String  SERVER_HOST= "https://admin.feeprint.com";
   static  final String TOKEN = "061447QP60810VRKDKZ";
   static  final String KEY_ID = "4472D819C7KDKZD5C14684";


    public static ApiServer getApiServer() {


        return new Retrofit.Builder()
                .baseUrl(SERVER_HOST)
                .callbackExecutor(Executors.newSingleThreadExecutor())
                .client(headerFileClient())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss")
                        .create()))
                .build()
                .create(ApiServer.class);
    }



    private static OkHttpClient headerFileClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .addHeader("Accept-Encoding", "gzip, deflate")
                            .addHeader("Connection", "keep-alive")
                            .addHeader("Accept", "*/*")
                            .build();
                    return chain.proceed(request);
                })
                .build();
    }





}
