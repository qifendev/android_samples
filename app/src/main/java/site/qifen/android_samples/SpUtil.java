package site.qifen.android_samples;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {
    private static SharedPreferences sp;
    private static final String xml_name="test";

    public static void putBoolean(Context ctx, String key, boolean value){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences(xml_name, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context ctx,String key,boolean defValue){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences(xml_name, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }


    public static void putString(Context ctx,String key,String value){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences(xml_name, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context ctx,String key,String defValue){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences(xml_name, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    public static void remove(Context ctx, String key) {
        if(sp == null){
            sp = ctx.getSharedPreferences(xml_name, Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).apply();
    }
}
