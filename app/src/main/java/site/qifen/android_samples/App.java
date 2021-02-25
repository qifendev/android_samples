package site.qifen.android_samples;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        String mode = SpUtil.getString(this, "theme", "light");
        if (mode.equals("dark"))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if(mode.equals("auto"))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }


}
