package ru.cotontidev.restapinotifications;

import org.apache.cordova.LOG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestApiNotificationsBootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications execute from receiver");
        context.startService(new Intent(context, RestApiNotificationsService.class));
    }
}
