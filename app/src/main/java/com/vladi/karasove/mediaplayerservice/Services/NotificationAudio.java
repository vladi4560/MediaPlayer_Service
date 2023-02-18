package com.vladi.karasove.mediaplayerservice.Services;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.vladi.karasove.mediaplayerservice.R;

public class NotificationAudio extends Notification {

    public static final String PREVIOUS_ACTION = "Previous";
    public static final String PAUSE_ACTION = "Pause";
    public static final String NEXT_ACTION = "Next";
    public static int NOTIFICATION_ID = 154;
    public static String CHANNEL_ID = "com.vladi.karasove.mediaplayerservice.CHANNEL_ID_FOREGROUND";
    private NotificationCompat.Builder notificationBuilder;
    private Context context;

    public NotificationAudio() {}

    private void setContext(Context context) {
        this.context = context;
    }

    public Notification notifyToUserForForegroundService(Context context,String songName) {
        Log.d("pttt",songName);
        setContext(context);
        Intent notificationIntent1 = new Intent(context, NotificationActionService.class);
        notificationIntent1.setAction(PREVIOUS_ACTION);
        notificationIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent1.putExtra(NotificationActionService.ACTION_NAME, PREVIOUS_ACTION);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, NOTIFICATION_ID, notificationIntent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Intent notificationIntent2 = new Intent(context, NotificationActionService.class);
        notificationIntent2.setAction(PAUSE_ACTION);
        notificationIntent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent2.putExtra(NotificationActionService.ACTION_NAME, PAUSE_ACTION);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, NOTIFICATION_ID, notificationIntent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent notificationIntent3 = new Intent(context, NotificationActionService.class);
        notificationIntent3.setAction(NEXT_ACTION);
        notificationIntent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent3.putExtra(NotificationActionService.ACTION_NAME, NEXT_ACTION);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, NOTIFICATION_ID, notificationIntent3, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        notificationBuilder = getNotificationBuilder(context,
                CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_LOW); //Low importance prevent visual appearance for this notification channel on top
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
        Bitmap backGround = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_background);
        notificationBuilder
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_music_card)
                .setLargeIcon(backGround)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_previous_button, PREVIOUS_ACTION, pendingIntent1) // #0
                .addAction(R.drawable.ic_pause_button, PAUSE_ACTION, pendingIntent2)  // #1
                .addAction(R.drawable.ic_next_button, NEXT_ACTION, pendingIntent3)// #2
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setContentTitle("Wonderful music")
                .setContentText(songName)
                .build();

        Notification notification = notificationBuilder.build();

        return notification;
    }
    public void updateSongName(String songName){
        notificationBuilder.setContentText(songName);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
    @SuppressLint("RestrictedApi")
    public void updateNotificationUI(String action,int button){
        Intent notificationIntent3 = new Intent(context, NotificationActionService.class);
        notificationIntent3.setAction(action);
        notificationIntent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent3.putExtra(NotificationActionService.ACTION_NAME, action);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, NOTIFICATION_ID, notificationIntent3, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.mActions.set(1,new NotificationCompat.Action(button, PAUSE_ACTION, pendingIntent3));
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    public static NotificationCompat.Builder getNotificationBuilder(Context context, String channelId, int importance) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(context, channelId, importance);
            builder = new NotificationCompat.Builder(context, channelId);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        return builder;
    }

    @TargetApi(26)
    private static void prepareChannel(Context context, String id, int importance) {
        final String appName = context.getString(R.string.app_name);
        String notifications_channel_description = "music channel";
        String description = notifications_channel_description;
        final NotificationManager nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        if (nm != null) {
            NotificationChannel nChannel = nm.getNotificationChannel(id);

            if (nChannel == null) {
                nChannel = new NotificationChannel(id, appName, importance);
                nChannel.setDescription(description);

                // from another answer
                nChannel.enableLights(true);
                nChannel.setLightColor(Color.BLUE);

                nm.createNotificationChannel(nChannel);
            }
        }
    }
}
