package site.qifen.android_samples;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    final int mCode = 1;
    private EditText edit;
    private ListView list;
    private Button btn;
    List<HashMap<String, String>> contactsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = findViewById(R.id.edit);
        list = findViewById(R.id.list);
        btn = findViewById(R.id.btn);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }


        readAllContacts();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findContactsByNum(edit.getText().toString());
            }
        });


    }


    private void findContactsByNum(String num) {

        Cursor cursor = null;
        try {
            //查询联系人数据
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.NUMBER + "= ?", new String[]{num}, null, null);
            //由于ContactsContract.CommonDataKinds.Phone类帮我们做好了封装，提供了一个CONTENT_URI常量，
            //而这个常量就是使用Uri.parse()方法解析出来的结果。
            if (cursor != null) {
                contactsList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    //获取联系人姓名
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //获取联系人手机号
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", displayName);
                    hashMap.put("num", number);
                    contactsList.add(hashMap);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        setData();

    }


    private void readAllContacts() {

        Cursor cursor = null;
        try {
            //查询联系人数据
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            //由于ContactsContract.CommonDataKinds.Phone类帮我们做好了封装，提供了一个CONTENT_URI常量，
            //而这个常量就是使用Uri.parse()方法解析出来的结果。
            if (cursor != null) {
                contactsList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    //获取联系人姓名
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //获取联系人手机号
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", displayName);
                    hashMap.put("num", number);
                    contactsList.add(hashMap);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
        setData();

    }


    public void setData() {
        if (contactsList.size() > 0) {
            list.setAdapter(new SimpleAdapter(this, contactsList, android.R.layout.simple_expandable_list_item_2, new String[]{"name", "num"}, new int[]{android.R.id.text1, android.R.id.text2}));
        }
    }


}

class Contacts {
    String name;
    String number;

    public Contacts(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}


