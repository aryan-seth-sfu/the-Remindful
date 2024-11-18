package com.example.theremindful2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
    // ibinder created so activities can be binded to music service and have direct access to it and its funcs
    private final IBinder binder = new LocalBinder();
    private static MediaPlayer mediaPlayer;
    private static int currentSongIndex = -1;


    // local binder allows other activities to be binded to the binder
    public class LocalBinder extends Binder {
        // allows user to access musicService methods
        MusicService getService() {
            return MusicService.this;
        }
    }

    // on bind simply returns the binder
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }


    public void setMediaPlayer(MediaPlayer player) {
//        if (mediaPlayer== null){
//            mediaPlayer = new MediaPlayer();
//
//        }
        // if a media player already exists and is different from the current one then we
        // release player to make sure the proper one is set

            if (mediaPlayer != null && mediaPlayer != player) {

                mediaPlayer.release();
            }
            // media player  points to the correct player
            mediaPlayer = player;
    }

    // function to get media player and access current one in usage
    public MediaPlayer getMediaPlayer() {

        return mediaPlayer;
    }

    // set the current index of the song to the index playing

    public void setCurrentSongIndex(int index) {

        currentSongIndex = index;
    }

    // gets the current index of the song playing
    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
