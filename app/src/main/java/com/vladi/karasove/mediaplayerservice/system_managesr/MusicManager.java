package com.vladi.karasove.mediaplayerservice.system_managesr;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.vladi.karasove.mediaplayerservice.Object.Audio;

import java.util.ArrayList;
import java.util.List;

public class MusicManager {

    private static MusicManager player;
    private int musicPosition=0;
    private ExoPlayer exoPlayer;
    private static Context context;
    private ArrayList<Audio> songList;
    private void setContext(Context contexts){
        context=contexts;
    }



    private MusicManager(Context context1) {
        exoPlayer = new ExoPlayer.Builder(context1).build();
        setContext(context1);
        songList = new ArrayList<>();
    }

    public static MusicManager getInstance(Context context) {
        if(player == null){
            player = new MusicManager(context);
        }
        return player;
    }
    public void fetchSongs() {

        Uri mediaStoreUri;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            //mediaStoreUri = Uri.parse("content://Internal storage/Music");
            Log.d("pttt",mediaStoreUri.toString());
        }else{
            mediaStoreUri =  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
        };

        try(Cursor cursor = context.getContentResolver().query(mediaStoreUri,projection,null,null,null)){

            int idColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn= cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn= cursor.getColumnIndexOrThrow( MediaStore.Audio.Media.DURATION);
            int sizeColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            //  int albumColumn= cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);


            while(cursor.moveToNext()){
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                //   long albumId = cursor.getLong(albumColumn);
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
                if(name.contains("mp3")&&!name.contains("AUD")){
                   // Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
                    name = name.substring(0,name.lastIndexOf("."));
                    Audio a = new Audio(name,uri,size,duration);
                    //Log.d("pttt","name:"+name+"/ uri"+uri+"/ size"+size+" / duration"+duration);
                    songList.add(a);
                }

            }

        }

    }
//    public void playNextSong() {
//        if(exoPlayer.hasNextMediaItem()){
//            musicPosition++;
//            exoPlayer.pause();
//            exoPlayer.seekToNext();
//            exoPlayer.prepare();
//            exoPlayer.play();
//        }
//    }
//    public void playPreviousSong(){
//        if(exoPlayer.hasPreviousMediaItem()){
//            musicPosition--;
//            exoPlayer.pause();
//            exoPlayer.seekToPrevious();
//            exoPlayer.prepare();
//            exoPlayer.play();
//        }
//    }
    public void playNextSong() {
        if(exoPlayer.hasNextMediaItem()&&(musicPosition<songList.size())){
            musicPosition++;
            exoPlayer.pause();
            exoPlayer.seekTo(musicPosition,0);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }
    public void playPreviousSong(){
        if(exoPlayer.hasPreviousMediaItem()&&(musicPosition> -1)){
            musicPosition--;
            exoPlayer.pause();
            exoPlayer.seekTo(musicPosition,0);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }


    public void playMusic(int position){
        if(!exoPlayer.isPlaying()){
                exoPlayer.setMediaItems(getMediaItems(),position,0);
            }else {
                exoPlayer.pause();
                exoPlayer.seekTo(position,0);
            }
            musicPosition=position;
            exoPlayer.prepare();
            exoPlayer.play();
    }
    private List<MediaItem> getMediaItems() {
        ArrayList<MediaItem> mediaItems= new ArrayList<>();
        for (Audio audio: songList) {
            MediaItem mediaItem= new MediaItem.Builder()
                    .setUri(audio.getUri())
                    .setMediaMetadata(getMetaData(audio))
                    .build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }



    private MediaMetadata getMetaData(Audio audio) {
        return new MediaMetadata.Builder()
                .setTitle(audio.getTitle())
                .build();
    }


    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public static Context getContext() {
        return context;
    }

    public ArrayList<Audio> getSongList() {
        return songList;
    }

    public int getMusicPosition() {return musicPosition;}
}
