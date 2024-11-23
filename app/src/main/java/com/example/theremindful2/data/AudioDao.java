package com.example.theremindful2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AudioDao {
    @Query("SELECT * FROM audio_entries")
    List<Audio> getAllAudio();

    @Query("SELECT * FROM audio_entries WHERE dateRecorded BETWEEN :startDate AND :endDate")
    List<Audio> getAudioByDateRange(long startDate, long endDate);

    @Query("SELECT * FROM audio_entries WHERE id = :audioId")
    Audio getAudioById(long audioId);

    @Insert
    long insertAudio(Audio audio);

    @Delete
    void deleteAudio(Audio audio);

    // Optional: Get latest recordings
    @Query("SELECT * FROM audio_entries ORDER BY dateRecorded DESC LIMIT :limit")
    List<Audio> getRecentAudio(int limit);

    // Optional: Search audio by title
    @Query("SELECT * FROM audio_entries WHERE title LIKE '%' || :searchQuery || '%'")
    List<Audio> searchAudio(String searchQuery);
}