package site.qifen.android_samples;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText url = findViewById(R.id.url);
        EditText show = findViewById(R.id.show);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url.getText().toString()))
                    ServerBuilder.getApiServer().waterMark(ServerBuilder.KEY_ID, ServerBuilder.TOKEN, url.getText().toString()).enqueue(new Callback<Result<Data>>() {
                        @Override
                        public void onResponse(Call<Result<Data>> call, Response<Result<Data>> response) {


                            Data data = response.body().getData();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    show.setText("title: " + data.getTitle() + "\nurl: " + data.getUrl() + "\nimg: " + data.getImg());

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


                                                File filesDir = getFilesDir();

                                                File file = new File(filesDir.getAbsoluteFile() + File.separator + UUID.randomUUID() + ".mp4");
//
//
                                                FileOutputStream outputStream = new FileOutputStream(file);

                                                outputStream.write(response.body().bytes());


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).start();


                                }
                            });


                        }

                        @Override
                        public void onFailure(Call<Result<Data>> call, Throwable t) {
                            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });


    }


}