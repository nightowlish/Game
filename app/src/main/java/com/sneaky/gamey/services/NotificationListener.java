package com.sneaky.gamey.services;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.service.notification.NotificationListenerService;

public class NotificationListener extends NotificationListenerService {

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        // Implement what you want here
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Implement what you want here
    }
}
