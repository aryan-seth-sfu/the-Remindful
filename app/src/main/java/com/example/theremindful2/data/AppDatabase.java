package com.example.theremindful2.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Image.class, Audio.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "image_database";
    private static volatile AppDatabase INSTANCE;

    public abstract ImageDao imageDao();
    public abstract AudioDao audioDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `audio_entries` " +
                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`title` TEXT NOT NULL, " +
                    "`audioPath` TEXT NOT NULL, " +
                    "`description` TEXT, " +
                    "`duration` INTEGER, " +
                    "`dateRecorded` INTEGER, " +
                    "`createdAt` INTEGER NOT NULL)");
        }
    };
}