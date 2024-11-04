package com.example.theremindful2.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM images")
    List<Image> getAllImages();

    @Query("SELECT * FROM images WHERE theme = :theme")
    List<Image> getImagesByTheme(String theme);

    @Insert
    long insertImage(Image image);

    @Delete
    void deleteImage(Image image);

    @Update
    void updateImage(Image image);
}