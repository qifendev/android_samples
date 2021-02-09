package site.qifen.android_samples;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //申请权限
        ActivityCompat.requestPermissions(this,new String[]{
            Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.WRITE_CONTACTS
        },1);




//    <uses-permission android:name="android.permission.READ_CALL_LOG" />
//    <uses-permission android:name="android.permission.WRITE_CALL_LOG" />
//    <uses-permission android:name="android.permission.READ_CONTACTS" />
//    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
//    <uses-permission android:name="android.permission.CALL_PHONE" />




    }


    public native String stringFromJNI();
}