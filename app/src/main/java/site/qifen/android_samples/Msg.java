package site.qifen.android_samples;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;


class Msg {
    private boolean isMms; //是mms，否则sms
    private String id; //id
    private String number; //号码
    private String subject; //主题
    private String date; //日期（时间戳）
    private String content; //短信内容
    private Bitmap bitmap; //mms的图片

    public Msg() {
    }

    public Msg(boolean isMms, String id, String number, String subject, String date, String content, Bitmap bitmap) {
        this.isMms = isMms;
        this.id = id;
        this.number = number;
        this.subject = subject;
        this.date = date;
        this.content = content;
        this.bitmap = bitmap;
    }

    public boolean isMms() {
        return isMms;
    }

    public void setMms(boolean mms) {
        isMms = mms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}