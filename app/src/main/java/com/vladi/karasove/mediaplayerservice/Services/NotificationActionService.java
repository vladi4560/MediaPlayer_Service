package com.vladi.karasove.mediaplayerservice.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    public static final String BROADCAST_CHANNEL="AUDIO_AUDIO";
    public static final String ACTION_NAME="actionName";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(BROADCAST_CHANNEL)
                .putExtra(ACTION_NAME,intent.getAction()));
    }
}
