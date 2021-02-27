package site.qifen.android_samples;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class ViewImageActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ListView listView = findViewById(R.id.listView);
        context = this;

        File filesDir = getFilesDir();

        File[] files = filesDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith("jpg"))
                    return true;
                else return false;
            }
        });

        if (files != null)
            listView.setAdapter(new BaseAdapter() {


                @Override
                public int getCount() {
                    return files.length;
                }

                @Override
                public Object getItem(int position) {
                    return files[position];
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ImageView imageView = new ImageView(context);
                    imageView.setAdjustViewBounds(true);
                    imageView.setMaxHeight(listView.getHeight() / 2);
                    imageView.setMaxWidth(listView.getWidth() / 2);
                    Bitmap bitmap = BitmapFactory.decodeFile(files[position].getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    if (bitmap.isRecycled()) bitmap.recycle();
                    return imageView;
                }
            });
    }
}













