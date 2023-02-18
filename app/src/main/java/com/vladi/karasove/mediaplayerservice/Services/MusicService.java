package com.vladi.karasove.mediaplayerservice.Services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.vladi.karasove.mediaplayerservice.MusicManager;
import com.vladi.karasove.mediaplayerservice.R;

public class MusicService extends Service {


    public static final String START_FOREGROUND_SERVICE = "START_FOREGROUND_SERVICE";
    public static final String STOP_FOREGROUND_SERVICE = "STOP_FOREGROUND_SERVICE";
    public static final String PREVIOUS_ACTION = "Previous";
    public static final String PAUSE_ACTION = "Pause";
    public static final String PLAY_ACTION = "Play";
    public static final String NEXT_ACTION = "Next";
    public static int NOTIFICATION_ID = 154;
    private int lastShownNotificationId = -1;
    public static String CHANNEL_ID = "com.vladi.karasove.mediaplayerservice.CHANNEL_ID_FOREGROUND";
    private NotificationAudio notificationAudio;
    private boolean isServiceRunningRightNow = false;
    private MusicManager mn;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopForeground(true);
            return START_NOT_STICKY;
        }

        if (intent.getAction().equals(START_FOREGROUND_SERVICE)) {
            if (isServiceRunningRightNow) {
                return START_STICKY;
            }
            mn=MusicManager.getInstance(getApplicationContext());
            notificationAudio = new NotificationAudio();
            Notification notification = notificationAudio.notifyToUserForForegroundService(getApplicationContext(),mn.getSongList().get(mn.getMusicPosition()).getTitle());
            startForeground(NOTIFICATION_ID, notification);
            isServiceRunningRightNow = true;
            broadCastInit();


        } else if (intent.getAction().equals(STOP_FOREGROUND_SERVICE)) {
            stopForeground(true);
            stopSelf();

            isServiceRunningRightNow = false;
            return START_NOT_STICKY;
        }
        return START_STICKY;
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString(NotificationActionService.ACTION_NAME);
            switch (action){
                case MusicService.PREVIOUS_ACTION:
                    mn.playPreviousSong();
                    notificationAudio.updateSongName(mn.getSongList().get(mn.getMusicPosition()).getTitle());
                    break;
                case MusicService.PAUSE_ACTION:
                    mn.getExoPlayer().pause();
                    notificationAudio.updateNotificationUI(PLAY_ACTION,R.drawable.ic_play_button);
                    break;
                case MusicService.NEXT_ACTION:
                    mn.playNextSong();
                    notificationAudio.updateSongName(mn.getSongList().get(mn.getMusicPosition()).getTitle());
                    break;
                case MusicService.PLAY_ACTION:
                    mn.getExoPlayer().play();
                    notificationAudio.updateNotificationUI(PAUSE_ACTION,R.drawable.ic_pause_button);
                    break;
            }
        }
    };

    private void broadCastInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(broadcastReceiver, new IntentFilter(NotificationActionService.BROADCAST_CHANNEL));
        }
    }
}
