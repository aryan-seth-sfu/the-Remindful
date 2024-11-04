package com.example.theremindful2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MusicTrackDao {
    @Query("SELECT * FROM music_tracks")
    List<MusicTrack> getAllTracks();

    @Query("UPDATE music_tracks SET play_count = play_count + 1 WHERE trackId = :trackId")
    void incrementPlayCount(int trackId);

    @Insert
    long insertTrack(MusicTrack track);

    @Delete
    void deleteTrack(MusicTrack track);
}