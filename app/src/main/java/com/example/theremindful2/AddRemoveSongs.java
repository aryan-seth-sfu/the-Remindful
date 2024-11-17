package com.example.theremindful2;

import android.net.Uri;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddRemoveSongs {

    // make the songs list static so it can continue to exist through activities
    private static List<Song> songs;
//    int currentSongIndex =0;

    // add new songArray List if doesnt exist
    public AddRemoveSongs() {
        if (songs == null) {
            songs = new ArrayList<>();
        }
    }

    public void newPlaylist() {
        // if playlist doesnt already exist then create a new playlist
         if (songs == null) {
            songs = new ArrayList<>();
        }
    }
    // add song to playlist
    public void addSong(Song song) {
        if (songs == null) {
            songs = new ArrayList<>();
        }
        songs.add(song);
    }

    public List<Song> allSongs() {
        // check if playlist is empty if it is create new instance
        if (songs == null) {

            songs = new ArrayList<>();
        }
        return songs;
    }

    public boolean isEmpty() {
        // check if playlist is empty
        return songs == null || songs.isEmpty();
    }


    // convert the songs to a json list to make it more readable
    public String toJson() {

        Gson gson = new Gson();

        return gson.toJson(songs);
    }

    // puts playlist into json format
    public void fromJson(String json) {
        Gson gson = new Gson();
        Song[] songArray = gson.fromJson(json, Song[].class);

        songs = new ArrayList<>();

        for (Song song : songArray) {

            songs.add(song);
        }
    }



}