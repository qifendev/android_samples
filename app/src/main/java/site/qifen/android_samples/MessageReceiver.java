package site.qifen.android_samples;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

//接收短信广播
    public  class MessageReceiver extends BroadcastReceiver {
    /**
     * 广播消息类型
     */
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action)) {
            System.err.println("receiver  sms");

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null && pdus.length > 0) {
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    int length = messages.length;
                    for (int i = 0; i < length; i++) {
                        byte[] pdu = (byte[]) pdus[i];
                        messages[i] = SmsMessage.createFromPdu(pdu);
                    }
                    for (SmsMessage msg : messages) {
                        // 获取短信内容
                        String content = msg.getMessageBody();
                        String sender = msg.getOriginatingAddress();

                        System.err.println("no activity: "+sender + ": " + content);
                    }
                }
            }
        }
    }
}
