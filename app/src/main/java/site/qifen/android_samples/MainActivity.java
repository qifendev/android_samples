package site.qifen.android_samples;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1: //下载完成
                    Toast.makeText(MainActivity.this, "保存相册成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2: //下载/解析失败
                    Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });


    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        final Uri uri = Uri.parse("content://media/external/video/media/504268");
//
//        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
//
//        startActivity(it);






        MobileAds.initialize(this, new OnInitializationCompleteListener() { //初始化广告
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                List<String> testDeviceIds = Arrays.asList("A86C4089EB9C9E707CE22137D45B901A");
//                RequestConfiguration configuration =
//                        new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//                MobileAds.setRequestConfiguration(configuration);
                AdView adView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest); //请求广告
            }
        });





        EditText url = findViewById(R.id.url);
        EditText show = findViewById(R.id.show);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url.getText().toString()))
                    ServerBuilder.getApiServer().waterMark(ServerBuilder.KEY_ID, ServerBuilder.TOKEN, url.getText().toString()).enqueue(new Callback<Result<Data>>() {
                        @Override
                        public void onResponse(Call<Result<Data>> call, Response<Result<Data>> response) {


                            if ((response != null) && (response.body() != null) && (response.body().getData() != null)) {


                                Data data = response.body().getData();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        show.setText("Title: " + data.getTitle() + "\nUrl: " + data.getUrl() + "\nImage: " + data.getImg());

                                    }
                                });


                                Request request = new Request.Builder().url(data.getUrl()).build();

                                new OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(okhttp3.Call call, IOException e) {
                                        System.out.println("fail:   " + e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {


                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {

//                                                    byte[] bytes = response.body().bytes();


//                                                    File filesDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
//
                                                    String fileName = UUID.randomUUID() + ".mp4";
//
//                                                    File file = new File(filesDir.getAbsoluteFile() + File.separator + fileName);
//
//                                                    System.out.println("path： " + file.getAbsolutePath());
////
////
//                                                    FileOutputStream outputStream = new FileOutputStream(file);
//
//                                                    outputStream.write(bytes);


                                                    String s = savePhoto(fileName, data.getTitle(), "video/mp4", "Movies/mov/", response.body().byteStream());


                                                    handler.sendEmptyMessage(1); //完成


//                                                    ContentValues values = new ContentValues();
//                                                    values.put(MediaStore.Video.Media.DISPLAY_NAME, UUID.randomUUID() + ".mp4");
//                                                    values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
//                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                                        values.put(MediaStore.Video.Media.RELATIVE_PATH, file.getAbsolutePath());
//                                                    }
//                                                    Uri insert = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);


//                                                    System.out.println("uri:  " + insert);


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    handler.sendEmptyMessage(2);
                                                }

                                            }
                                        }).start();


                                    }
                                });


                            } else {
                                handler.sendEmptyMessage(2);
                            }


                        }

                        @Override
                        public void onFailure(Call<Result<Data>> call, Throwable t) {
                            handler.sendEmptyMessage(2);
                        }
                    });
            }
        });


    }


    private String savePhoto(String name, String description, String mime, String path, InputStream inputStream) {
        if (path.startsWith("/")) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, name);
        values.put(MediaStore.Video.Media.MIME_TYPE, mime);
        values.put(MediaStore.Video.Media.DESCRIPTION, description);
        values.put(MediaStore.Video.Media.RELATIVE_PATH, path);

        Uri url = null;
        String stringUri = null;
        ContentResolver cr = getContentResolver();
        try {
            url = cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            if (url == null) {
                return null;
            }
            byte[] buffer = new byte[1024];
            ParcelFileDescriptor descriptor = cr.openFileDescriptor(url, "w");
            FileOutputStream outputStream = new FileOutputStream(descriptor.getFileDescriptor());
            while (true) {
                int readSize = inputStream.read(buffer);
                if (readSize == -1) {
                    break;
                }
                outputStream.write(buffer, 0, readSize);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            if (url != null) {
                cr.delete(url, null, null);
            }
        }
        if (url != null) {
            stringUri = url.toString();
        }
        return stringUri;
    }


}