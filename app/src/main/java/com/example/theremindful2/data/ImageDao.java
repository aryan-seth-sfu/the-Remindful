package com.example.theremindful2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM images ORDER BY createdAt DESC")
    List<Image> getAllImages();

    @Query("SELECT * FROM images WHERE theme = :themeName")
    List<Image> getImagesByTheme(String themeName);

    @Insert
    long insertImage(Image image);

    @Delete
    void deleteImage(Image image);

    @Query("SELECT DISTINCT theme FROM images WHERE theme IS NOT NULL")
    List<String> getAllThemes();

    @Update
    void updateImage(Image image);
}