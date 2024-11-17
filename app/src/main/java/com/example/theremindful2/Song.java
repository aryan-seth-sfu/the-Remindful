package com.example.theremindful2;

import android.net.Uri;

public class Song {

    private Uri uri;

    public Song(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
