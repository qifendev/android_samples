package site.qifen.android_samples;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


//来电广播
public class CallReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (action.equals("android.intent.action.PHONE_STATE"))
        {	// 接收到来电广播，执行来电监听处理逻辑
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private final PhoneStateListener listener = new PhoneStateListener()
    {
        @Override
        public void onCallStateChanged(int state, String phoneNumber)
        {
            super.onCallStateChanged(state, phoneNumber);
            switch (state)
            {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲/挂断处理逻辑
                    Log.e("phone", "onCallStateChanged: " + phoneNumber);

//                    context.getContentResolver().delete(CallLog.Calls.CONTENT_URI,phoneNumber ,null); //删除所有这个号码的记录

//                    来电：CallLog.Calls.INCOMING_TYPE （常量值：1）
//                    已拨：CallLog.Calls.OUTGOING_TYPE（常量值：2）
//                    未接：CallLog.Calls.MISSED_TYPE（常量值：3）


                    //删除挂断电话
                    Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { "_id" }, "number=? and (type=1 or type=3)", new String[] { phoneNumber },
                            "_id desc limit 1");
                    if (cursor.moveToFirst()) {
                        int id = cursor.getInt(0);
                        context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[] { id + "" });
                    }
                    cursor.close();



                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:// 接听处理逻辑
                    break;

                case TelephonyManager.CALL_STATE_RINGING:// 来电处理逻辑
                    break;
            }
        }
    };





}



