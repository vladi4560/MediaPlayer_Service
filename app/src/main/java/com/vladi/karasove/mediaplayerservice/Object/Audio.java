package com.vladi.karasove.mediaplayerservice.Object;

import android.net.Uri;

public class Audio {

    String title;
    Uri uri;
    int size;
    int duration;

    public Audio(String title, Uri uri, int size, int duration) {
        this.title = title;
        this.uri = uri;
        this.size = size;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
