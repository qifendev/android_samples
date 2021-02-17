package site.qifen.android_samples;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContentResolver contentResolver;
    int num = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentResolver = getContentResolver();


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_MMS, Manifest.permission.RECEIVE_MMS}, 1);


        List<Msg> mmsAndSMS = getMMSAndSMS();

        ListView list = findViewById(R.id.list);
        String[] strings = null;
        if (mmsAndSMS != null && !mmsAndSMS.isEmpty()) {
            strings= new String[mmsAndSMS.size()];
            for (int i = 0; i < mmsAndSMS.size(); i++) {
                strings[i] = mmsAndSMS.get(i).getNumber() + "\n" + mmsAndSMS.get(i).getContent();
            }
        }

        list.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,strings));


    }


    //反回所有短信
    private List<Msg> getMMSAndSMS() {
        List<Msg> msgList = new ArrayList<>();
        final String[] projection = new String[]{"_id", "ct_t"};
        final String[] msgPro = new String[]{"*"};
        Uri uri = Uri.parse("content://mms-sms/conversations/");
        Cursor msgCursor = contentResolver.query(uri, projection, null, null, null);
        if (msgCursor != null) {
            if (msgCursor.moveToFirst()) {
                do {
                    num++;
                    String string = msgCursor.getString(msgCursor.getColumnIndex("ct_t"));
                    String id = msgCursor.getString(msgCursor.getColumnIndex("_id"));
                    if ("application/vnd.wap.multipart.related".equals(string)) { //判断是否为mms
                        // 这个短信是MMS
                        Uri mmsUri = Uri.parse("content://mms/");
                        String selection = "_id = " + id;
                        Cursor mmsCursor = contentResolver.query(mmsUri, null, selection, null, null);
                        if (mmsCursor != null && mmsCursor.moveToFirst()) {
                            String sub = mmsCursor.getString(mmsCursor.getColumnIndex("sub"));
                            String date = mmsCursor.getString(mmsCursor.getColumnIndex("date"));
                            String selectionPart = "mid=" + id;
                            Uri partUri = Uri.parse("content://mms/part");
                            Cursor cursor = contentResolver.query(partUri, null,
                                    selectionPart, null, null);
                            String body = null;
                            String mid = null;
                            Bitmap bitmap = null;
                            String partId = null;
                            if (cursor != null & cursor.moveToFirst()) {
                                do {
                                    partId = cursor.getString(cursor.getColumnIndex("_id"));//mms的id
                                    String type = cursor.getString(cursor.getColumnIndex("ct")); //获取mms的类型
                                    mid = cursor.getString(cursor.getColumnIndex("mid"));
                                    if ("text/plain".equals(type)) {
                                        String data = cursor.getString(cursor.getColumnIndex("_data"));
                                        if (data != null) {
                                            body = getMmsText(partId);
                                        } else {
                                            body = cursor.getString(cursor.getColumnIndex("text"));
                                        }
                                    } else if ("image/jpeg".equals(type) || "image/bmp".equals(type) ||
                                            "image/gif".equals(type) || "image/jpg".equals(type) ||
                                            "image/png".equals(type)) {
                                        bitmap = getMmsImage(partId);
                                    }
                                } while (cursor.moveToNext());
                            }

                            msgList.add(new Msg(true, id, getAddressNumber(partId), new String(sub.getBytes(Charset.forName("ISO8859_1")), StandardCharsets.UTF_8), date, body, bitmap));
                        }
                    } else {
                        // 这个短信是SMS
                        Uri smsUri = Uri.parse("content://sms");
                        Cursor smsCursor = contentResolver.query(smsUri, msgPro, "_id=?", new String[]{id}, null);
                        if (smsCursor != null && smsCursor.moveToNext()) {
                            String body = smsCursor.getString(smsCursor.getColumnIndex("body"));
                            String subject = smsCursor.getString(smsCursor.getColumnIndex("subject"));
                            String date = smsCursor.getString(smsCursor.getColumnIndex("date"));
                            String address = smsCursor.getString(smsCursor.getColumnIndex("address"));
                            msgList.add(new Msg(false, id, address, subject, date, body, null));
                            smsCursor.close();
                        }
                    }
                } while (msgCursor.moveToNext());
            }
            msgCursor.close();
        } else {
            return null;
        }
        return msgList;
    }


    //获取mms的文本
    private String getMmsText(String id) {
        Uri partURI = Uri.parse("content://mms/part/" + id);
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = contentResolver.openInputStream(partURI);
            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr);
                String temp = reader.readLine();
                while (temp != null) {
                    sb.append(temp);
                    temp = reader.readLine();
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }


    //获取mms的图片
    private Bitmap getMmsImage(String _id) {
        Uri partURI = Uri.parse("content://mms/part/" + _id);
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = contentResolver.openInputStream(partURI);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException ignored) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }


    //获取mms电话号码
    private String getAddressNumber(String id) {
        String selectionAdd = "msg_id=" + id;
        String uriStr = MessageFormat.format("content://mms/{0}/addr", id);
        Uri uriAddress = Uri.parse(uriStr);
        Cursor cAdd = contentResolver.query(uriAddress, null,
                selectionAdd, null, null);
        String name = null;
        if (cAdd != null && cAdd.moveToFirst()) {
            do {
                String number = cAdd.getString(cAdd.getColumnIndex("address"));
                if (number != null) {
                    try {
                        Long.parseLong(number.replace("-", ""));
                        name = number;
                    } catch (NumberFormatException nfe) {
                        if (name == null) {
                            name = number;
                        }
                    }
                }
            } while (cAdd.moveToNext());
            cAdd.close();
        }
        return name;
    }


}