package com.vladi.karasove.mediaplayerservice.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.vladi.karasove.mediaplayerservice.Adapter.PlaylistAdapter;
import com.vladi.karasove.mediaplayerservice.CallBack.CallBack_AdapterToMain;
import com.vladi.karasove.mediaplayerservice.CallBack.CallBack_SensorToMain;
import com.vladi.karasove.mediaplayerservice.system_managesr.MusicManager;
import com.vladi.karasove.mediaplayerservice.system_managesr.SensorMangerACC;
import com.vladi.karasove.mediaplayerservice.Services.MusicService;
import com.vladi.karasove.mediaplayerservice.Object.Audio;
import com.vladi.karasove.mediaplayerservice.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ExtendedFloatingActionButton previousBtn, playBtn, nextBtn, acceleBtn;
    private TextView noMusic, realTime, endTime, currentSong;
    private SeekBar seekBar;
    private ImageView backGround;

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private LinearLayoutManager manager;

    private MusicManager musicManager;
    private SensorMangerACC accelerometerManger;
    private boolean accelerometerActive = false;
    private final int REQUEST_CODE_PERMISSION_MEDIA = 101;
    private static final int MANUALLY_CONTACTS_PERMISSION_REQUEST_MEDIA = 103;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accelerometerManger = new SensorMangerACC(getApplicationContext());
        requestMediaPermission();
        musicManager = MusicManager.getInstance(this);

    }


    private void requestMediaPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_MEDIA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_MEDIA: {
                boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (result) {
                    musicManager.fetchSongs();
                    findViews();
                    userControls();
                    return;
                }
                requestPermissionWithRationaleCheck();
                return;
            }

        }
    }

    private void requestPermissionWithRationaleCheck() {
        String message = "An approved needed for reading media files";
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog alertDialog =
                    new AlertDialog.Builder(this)
                            .setMessage(message)
                            .setPositiveButton(getString(android.R.string.ok),
                                    (dialog, which) -> {
                                        requestMediaPermission();
                                        dialog.cancel();
                                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // disabled functions due to denied permissions
                                }
                            })
                            .show();
            alertDialog.setCanceledOnTouchOutside(true);
        } else {
            openPermissionSettingDialog();
        }
    }

    private void openPermissionSettingDialog() {
        String message = "for granting permission go to the settings of the application.";
        AlertDialog alertDialog =
                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setPositiveButton(getString(android.R.string.ok),
                                (dialog, which) -> {
                                    openSettingsManually();
                                    dialog.cancel();
                                }).show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void openSettingsManually() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, MANUALLY_CONTACTS_PERMISSION_REQUEST_MEDIA);
        this.finish();
        System.exit(0);
    }

    private void findViews() {
        backGround = findViewById(R.id.main_IMG_backGround);
        Glide.with(this).load("https://w0.peakpx.com/wallpaper/352/71/HD-wallpaper-black-and-white-music-outline-simple.jpg").into(backGround);
        previousBtn = findViewById(R.id.main_BTN_previousSong);
        playBtn = findViewById(R.id.main_BTN_playSong);
        nextBtn = findViewById(R.id.main_BTN_nextSong);
        acceleBtn = findViewById(R.id.main_BTN_ACC);
        recyclerView = findViewById(R.id.main_RYC_playlist);
        noMusic = findViewById(R.id.main_TXT_noMusic);
        seekBar = findViewById(R.id.main_SKR_playerDuration);
        realTime = findViewById(R.id.main_TXT_realTime);
        endTime = findViewById(R.id.main_TXT_endTime);
        currentSong = findViewById(R.id.main_TXT_currentSongName);
        ArrayList<Audio> audioList = musicManager.getSongList();
        if (audioList.size() == 0) {
            noMusic.setVisibility(View.VISIBLE);
        } else {
            noMusic.setVisibility(View.INVISIBLE);
            adapter = new PlaylistAdapter(this, audioList);
            adapter.setCallBack_adapterToPlaylist(callBack_playListToPlayNow);
            recyclerView.setAdapter(adapter);
            manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }

    }


    private void userControls() {
        nextBtn.setOnClickListener(view -> playNextSong());
        playBtn.setOnClickListener(view -> play_pauseSong());
        previousBtn.setOnClickListener(view -> playPreviousSong());
        acceleBtn.setOnClickListener(view -> accelFeature());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressValue = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicManager.getExoPlayer().getPlaybackState() == ExoPlayer.STATE_READY) {
                    seekBar.setProgress(progressValue);
                    realTime.setText(getReadableTime(progressValue));
                    musicManager.getExoPlayer().seekTo(progressValue);
                }
            }
        });
    }

    private void accelFeature() {
        if(musicManager.getSongList().size() < 2){
            return;
        }
        if (accelerometerActive == false) {
            acceleBtn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            accelerometerManger.startSensor();
            accelerometerManger.setCallBack_sensorToMain(callBack_sensorToMain);
            accelerometerActive = true;
        } else {
            acceleBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.application_purple)));
            accelerometerManger.StopSensor();
            accelerometerActive = false;
        }
    }

    private void play_pauseSong() {
        if(musicManager.getSongList().size() == 0){
            return;
        }
        if (musicManager.getExoPlayer().isPlaying()) {
            playBtn.setIcon(getDrawable(R.drawable.ic_play_button));
            musicManager.getExoPlayer().pause();
        } else {
            playBtn.setIcon(getDrawable(R.drawable.ic_pause_button));
            musicManager.getExoPlayer().play();
        }
    }

    private void playPreviousSong() {
        if(musicManager.getSongList().size() == 0){
            return;
        }
        musicManager.playPreviousSong();
        updateUI();
    }


    private void playNextSong() {
        if(musicManager.getSongList().size() == 0){
            return;
        }
        musicManager.playNextSong();
        updateUI();
    }


    private void updateUI() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                playBtn.setIcon(getDrawable(R.drawable.ic_pause_button));
                endTime.setText(getReadableTime((int) musicManager.getExoPlayer().getDuration()));
                seekBar.setMax((int) musicManager.getExoPlayer().getDuration());
               // Log.d("pttt",musicManager.getSongList().get(musicManager.getMusicPosition()).getTitle());
                currentSong.setText(musicManager.getSongList().get(musicManager.getMusicPosition()).getTitle());
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (musicManager.getExoPlayer().isPlaying()) {

                            seekBar.setProgress((int) musicManager.getExoPlayer().getCurrentPosition());
                            realTime.setText(getReadableTime((int) musicManager.getExoPlayer().getCurrentPosition()));
                        }
                        new Handler().postDelayed(this, 1000);
                    }
                });
            }

        }, 500);// give the ExoPlayer time to prepare

    }

    public String getReadableTime(long duration) {
        String time;
        int hrs = (int) (duration / (1000 * 60 * 60));
        int min = (int) (duration % (1000 * 60 * 60)) / (1000 * 60);
        int secs = (int) (((duration % (1000 * 60 * 60)) % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        if (hrs < 1) {
            time = min + ":" + secs;
        } else {
            time = hrs + ":" + min + ":" + secs;
        }
        return time;
    }


    @Override
    protected void onStop() {
        super.onStop();
        // if changing line 271 to comment the service will run with the sensor
        accelerometerManger.StopSensor();
        if (musicManager.getExoPlayer().isPlaying()) {
            startService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
        musicManager.getExoPlayer().pause();
        musicManager.getExoPlayer().release();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        stopService();
    }


    private void startService() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.START_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopService() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.STOP_FOREGROUND_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);

        } else {
            startService(intent);
        }
    }

    CallBack_AdapterToMain callBack_playListToPlayNow = new CallBack_AdapterToMain() {
        @Override
        public void clickedOnAudio(int position) {
            musicManager.playMusic(position);
            updateUI();
        }
    };

    CallBack_SensorToMain callBack_sensorToMain = new CallBack_SensorToMain() {
        @Override
        public void sensorSongChange(int x) {
            if (x < 0) {
                playNextSong();
            } else if (x > 0) {
                playPreviousSong();
            }
        }
    };
}