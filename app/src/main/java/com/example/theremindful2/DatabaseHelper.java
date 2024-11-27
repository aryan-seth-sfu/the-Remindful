package com.example.theremindful2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "theremindful_db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_MEDIA = "media_items";

    private static final String TABLE_THEME = "themes";
    private static final String TABLE_MEDIA_THEME = "media_theme";

    // Common Columns
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // Media table columns
    private static final String KEY_FILE_PATH = "file_path";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TYPE = "type"; // "image" or "audio"
    private static final String KEY_LIKES = "likes";

    // Theme table columns
    private static final String KEY_THEME_NAME = "name";

    // Media_Theme junction table columns
    private static final String KEY_MEDIA_ID = "media_id";
    private static final String KEY_THEME_ID = "theme_id";

    // Singleton instance and context
    private static DatabaseHelper instance;
    private static Context ctx;

    // Create table SQL queries
    private static final String CREATE_MEDIA_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MEDIA + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_FILE_PATH + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_TYPE + " TEXT,"
                    + KEY_LIKES + " INTEGER DEFAULT 0, "
                    + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";


    private static final String CREATE_THEME_TABLE =
            "CREATE TABLE " + TABLE_THEME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_THEME_NAME + " TEXT UNIQUE,"
                    + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private static final String CREATE_MEDIA_THEME_TABLE =
            "CREATE TABLE " + TABLE_MEDIA_THEME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_MEDIA_ID + " INTEGER,"
                    + KEY_THEME_ID + " INTEGER,"
                    + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY(" + KEY_MEDIA_ID + ") REFERENCES " + TABLE_MEDIA + "(" + KEY_ID + "),"
                    + "FOREIGN KEY(" + KEY_THEME_ID + ") REFERENCES " + TABLE_THEME + "(" + KEY_ID + ")"
                    + ")";

    // SQL for creating theme interaction table
    private static final String CREATE_THEME_INTERACTION_TABLE =
            "CREATE TABLE theme_interaction (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "theme_id INTEGER, " +
                    "interaction_count INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(theme_id) REFERENCES themes(id))";

    // SQL for creating photo interaction table
    private static final String CREATE_PHOTO_INTERACTION_TABLE =
            "CREATE TABLE photo_interaction (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "media_id INTEGER, " +
                    "interaction_count INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(media_id) REFERENCES media_items(id))";



    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context.getApplicationContext();
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEDIA_TABLE);
        db.execSQL(CREATE_THEME_TABLE);
        db.execSQL(CREATE_MEDIA_THEME_TABLE);
        db.execSQL(CREATE_THEME_INTERACTION_TABLE);
        db.execSQL(CREATE_PHOTO_INTERACTION_TABLE);

        logTableSchema(db, TABLE_MEDIA);
    }

    private void logTableSchema(SQLiteDatabase db, String tableName) {
        try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null)) {
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(1);
                String columnType = cursor.getString(2);
                Log.d(TAG, "Table " + tableName + " has column: " + columnName + " of type " + columnType);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_MEDIA + " ADD COLUMN " + KEY_LIKES + " INTEGER DEFAULT 0;");
            } catch (Exception e) {
                Log.e(TAG, "Error adding 'likes' column, recreating table: " + e.getMessage());
                // Drop the old table and recreate it if adding the column fails
                recreateMediaTable(db);
            }
        }

        if (oldVersion < 3) {
            db.execSQL(CREATE_MEDIA_THEME_TABLE); // Add media_theme table if not present
        }
    }

    private void recreateMediaTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        db.execSQL(CREATE_MEDIA_TABLE);
    }

    // Ensure all tables exist
    private void ensureTablesExist(SQLiteDatabase db) {
        db.execSQL(CREATE_MEDIA_TABLE);
        db.execSQL(CREATE_THEME_TABLE);
        db.execSQL(CREATE_MEDIA_THEME_TABLE);
    }

    // Add a new media item
    public long addMediaItem(String filePath, String description, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FILE_PATH, filePath);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_TYPE, type);
        values.put(KEY_LIKES, 0);
        return db.insert(TABLE_MEDIA, null, values);
    }

    // Add a new theme
    public long addTheme(String themeName) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First check if theme exists
        Cursor cursor = db.query(TABLE_THEME, new String[]{KEY_ID},
                KEY_THEME_NAME + "=?", new String[]{themeName}, null, null, null);

        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        cursor.close();

        // If theme doesn't exist, create it
        ContentValues values = new ContentValues();
        values.put(KEY_THEME_NAME, themeName);
        return db.insert(TABLE_THEME, null, values);
    }

    // Associate media with theme
    public void addMediaTheme(long mediaId, long themeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if association already exists
        Cursor cursor = db.query(TABLE_MEDIA_THEME, new String[]{KEY_ID},
                KEY_MEDIA_ID + "=? AND " + KEY_THEME_ID + "=?",
                new String[]{String.valueOf(mediaId), String.valueOf(themeId)},
                null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(KEY_MEDIA_ID, mediaId);
            values.put(KEY_THEME_ID, themeId);
            db.insert(TABLE_MEDIA_THEME, null, values);
        }
        cursor.close();
    }

    // Get all images for a specific theme
    public List<String> getImagesByTheme(String theme) {
        List<String> imageList = new ArrayList<>();
        String selectQuery = "SELECT m." + KEY_FILE_PATH +
                " FROM " + TABLE_MEDIA + " m" +
                " INNER JOIN " + TABLE_MEDIA_THEME + " mt ON m." + KEY_ID + " = mt." + KEY_MEDIA_ID +
                " INNER JOIN " + TABLE_THEME + " t ON mt." + KEY_THEME_ID + " = t." + KEY_ID +
                " WHERE t." + KEY_THEME_NAME + " = ?" +
                " AND m." + KEY_TYPE + " = 'image'" +
                " ORDER BY m." + KEY_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{theme});

        if (cursor.moveToFirst()) {
            do {
                imageList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return imageList;
    }

    // Get all audio files for a specific theme
    public List<String> getAudioByTheme(String theme) {
        List<String> audioList = new ArrayList<>();
        String selectQuery = "SELECT m." + KEY_FILE_PATH +
                " FROM " + TABLE_MEDIA + " m" +
                " INNER JOIN " + TABLE_MEDIA_THEME + " mt ON m." + KEY_ID + " = mt." + KEY_MEDIA_ID +
                " INNER JOIN " + TABLE_THEME + " t ON mt." + KEY_THEME_ID + " = t." + KEY_ID +
                " WHERE t." + KEY_THEME_NAME + " = ?" +
                " AND m." + KEY_TYPE + " = 'audio'" +
                " ORDER BY m." + KEY_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{theme});

        if (cursor.moveToFirst()) {
            do {
                audioList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return audioList;
    }

    // Get all themes
    public List<String> getAllThemes() {
        List<String> themes = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + KEY_THEME_NAME +
                " FROM " + TABLE_THEME +
                " ORDER BY " + KEY_THEME_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                themes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return themes;
    }

    // Get description for a specific file
    public String getDescription(String filePath) {
        String description = null;
        String selectQuery = "SELECT " + KEY_DESCRIPTION +
                " FROM " + TABLE_MEDIA +
                " WHERE " + KEY_FILE_PATH + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{filePath});

        if (cursor.moveToFirst()) {
            description = cursor.getString(0);
        }
        cursor.close();
        return description;
    }

    // Update description
    public int updateDescription(String filePath, String newDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DESCRIPTION, newDescription);
        return db.update(TABLE_MEDIA, values, KEY_FILE_PATH + " = ?", new String[]{filePath});
    }

    // Delete media item
    public void deleteMediaItem(String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First get the media ID
        Cursor cursor = db.query(TABLE_MEDIA, new String[]{KEY_ID},
                KEY_FILE_PATH + "=?", new String[]{filePath}, null, null, null);

        if (cursor.moveToFirst()) {
            long mediaId = cursor.getLong(0);

            // Delete from media_theme junction table
            db.delete(TABLE_MEDIA_THEME, KEY_MEDIA_ID + "=?",
                    new String[]{String.valueOf(mediaId)});

            // Delete from media table
            db.delete(TABLE_MEDIA, KEY_FILE_PATH + "=?", new String[]{filePath});
        }
        cursor.close();
    }

    // Add an Image with multiple themes
    public boolean addImage(Context context, Uri imageUri, List<String> themes, String description) {
        if (context == null || imageUri == null || themes == null || themes.isEmpty()) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        db.beginTransaction();

        try {
            // First, save the image file
            File baseDir = new File(context.getFilesDir(), "images");
            baseDir.mkdirs();
            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(baseDir, fileName);
            String filePath = imageFile.getAbsolutePath();

            // Copy image file
            try (InputStream is = context.getContentResolver().openInputStream(imageUri);
                 OutputStream os = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
// Add media entry
            long mediaId = addMediaItem(filePath, description, "image");
            if (mediaId == -1) {
                throw new Exception("Failed to insert media entry");
            }

            // Add or get themes and create associations
            for (String themeName : themes) {
                // Get or create theme
                long themeId = addTheme(themeName);
                if (themeId == -1) {
                    throw new Exception("Failed to process theme: " + themeName);
                }

                // Create association
                addMediaTheme(mediaId, themeId);
            }

            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error in addImage: " + e.getMessage());
            // If error occurs, delete the saved file
//            File imageFile = new File(baseDir, fileName);
//            if (imageFile.exists()) {
//                imageFile.delete();
//            }
        } finally {
            db.endTransaction();
        }

        return success;
    }

    // Get all themes for a specific media item
    public List<Theme> filterThemesByImagePath(String imagePath) {
        List<Theme> themes = new ArrayList<>();
        String selectQuery = "SELECT t." + KEY_THEME_NAME + ", m." + KEY_FILE_PATH +
                " FROM " + TABLE_THEME + " t" +
                " INNER JOIN " + TABLE_MEDIA_THEME + " mt ON t." + KEY_ID + " = mt." + KEY_THEME_ID +
                " INNER JOIN " + TABLE_MEDIA + " m ON mt." + KEY_MEDIA_ID + " = m." + KEY_ID +
                " WHERE m." + KEY_FILE_PATH + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{imagePath});

        if (cursor.moveToFirst()) {
            do {
                String themeName = cursor.getString(0);
                List<String> themePaths = getImagesByTheme(themeName);
                themes.add(new Theme(themeName, themePaths));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return themes;
    }

    // Remove image from a theme
    public boolean removeImageFromTheme(String imagePath, String themeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = false;
        db.beginTransaction();

        try {
            // Get media ID and theme ID
            String mediaQuery = "SELECT " + KEY_ID + " FROM " + TABLE_MEDIA +
                    " WHERE " + KEY_FILE_PATH + " = ?";
            String themeQuery = "SELECT " + KEY_ID + " FROM " + TABLE_THEME +
                    " WHERE " + KEY_THEME_NAME + " = ?";

            long mediaId = -1;
            long themeId = -1;

            Cursor mediaCursor = db.rawQuery(mediaQuery, new String[]{imagePath});
            if (mediaCursor.moveToFirst()) {
                mediaId = mediaCursor.getLong(0);
            }
            mediaCursor.close();

            Cursor themeCursor = db.rawQuery(themeQuery, new String[]{themeName});
            if (themeCursor.moveToFirst()) {
                themeId = themeCursor.getLong(0);
            }
            themeCursor.close();

            if (mediaId != -1 && themeId != -1) {
                // Remove the association
                int deleted = db.delete(TABLE_MEDIA_THEME,
                        KEY_MEDIA_ID + " = ? AND " + KEY_THEME_ID + " = ?",
                        new String[]{String.valueOf(mediaId), String.valueOf(themeId)});

                if (deleted > 0) {
                    // Check if this was the last theme for this media
                    Cursor remainingThemes = db.query(TABLE_MEDIA_THEME,
                            new String[]{KEY_ID},
                            KEY_MEDIA_ID + " = ?",
                            new String[]{String.valueOf(mediaId)},
                            null, null, null);

                    if (!remainingThemes.moveToFirst()) {
                        // No more themes, delete the media entry and file
                        db.delete(TABLE_MEDIA, KEY_ID + " = ?",
                                new String[]{String.valueOf(mediaId)});
                        File mediaFile = new File(imagePath);
                        if (mediaFile.exists()) {
                            mediaFile.delete();
                        }
                    }
                    remainingThemes.close();
                    success = true;
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error removing image from theme: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return success;
    }

    // Get all themes with Images
    public List<Theme> getAllThemesWithImages() {
        List<Theme> themesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // First get all unique themes that have images
        String themeQuery = "SELECT DISTINCT t." + KEY_THEME_NAME +
                " FROM " + TABLE_THEME + " t" +
                " INNER JOIN " + TABLE_MEDIA_THEME + " mt ON t." + KEY_ID + " = mt." + KEY_THEME_ID +
                " INNER JOIN " + TABLE_MEDIA + " m ON mt." + KEY_MEDIA_ID + " = m." + KEY_ID +
                " WHERE m." + KEY_TYPE + " = 'image'" +
                " ORDER BY t." + KEY_THEME_NAME + " ASC";

        Cursor themeCursor = db.rawQuery(themeQuery, null);

        try {
            if (themeCursor.moveToFirst()) {
                do {
                    String themeName = themeCursor.getString(0);

                    // For each theme, get all its images
                    String imageQuery = "SELECT m." + KEY_FILE_PATH +
                            " FROM " + TABLE_MEDIA + " m" +
                            " INNER JOIN " + TABLE_MEDIA_THEME + " mt ON m." + KEY_ID + " = mt." + KEY_MEDIA_ID +
                            " INNER JOIN " + TABLE_THEME + " t ON mt." + KEY_THEME_ID + " = t." + KEY_ID +
                            " WHERE t." + KEY_THEME_NAME + " = ?" +
                            " AND m." + KEY_TYPE + " = 'image'" +
                            " ORDER BY m." + KEY_CREATED_AT + " DESC";

                    Cursor imageCursor = db.rawQuery(imageQuery, new String[]{themeName});
                    List<String> photos = new ArrayList<>();

                    if (imageCursor.moveToFirst()) {
                        do {
                            photos.add(imageCursor.getString(0));
                        } while (imageCursor.moveToNext());
                    }
                    imageCursor.close();

                    // Only add themes that actually have photos
                    if (!photos.isEmpty()) {
                        themesList.add(new Theme(themeName, photos));
                    }

                } while (themeCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting themes with images: " + e.getMessage());
        } finally {
            themeCursor.close();
        }

        return themesList;
    }

    public void logThemeInteraction(long themeId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if theme interaction exists
        Cursor cursor = db.query("theme_interaction", new String[]{"interaction_count"},
                "theme_id=?", new String[]{String.valueOf(themeId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            // Update interaction count
            int currentCount = cursor.getInt(0);
            ContentValues values = new ContentValues();
            values.put("interaction_count", currentCount + 1);
            db.update("theme_interaction", values, "theme_id=?", new String[]{String.valueOf(themeId)});
        } else {
            // Insert new interaction record
            ContentValues values = new ContentValues();
            values.put("theme_id", themeId);
            values.put("interaction_count", 1);
            db.insert("theme_interaction", null, values);
        }
        cursor.close();
    }

    public void logPhotoInteraction(long mediaId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if photo interaction exists
        Cursor cursor = db.query("photo_interaction", new String[]{"interaction_count"},
                "media_id=?", new String[]{String.valueOf(mediaId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            // Update interaction count
            int currentCount = cursor.getInt(0);
            ContentValues values = new ContentValues();
            values.put("interaction_count", currentCount + 1);
            db.update("photo_interaction", values, "media_id=?", new String[]{String.valueOf(mediaId)});
            Log.d(TAG, "logPhotoInteraction: Updated interaction count for mediaId " + mediaId + " to " + (currentCount + 1));
        } else {
            // Insert new interaction record
            ContentValues values = new ContentValues();
            values.put("media_id", mediaId);
            values.put("interaction_count", 1);
            db.insert("photo_interaction", null, values);
            Log.d(TAG, "logPhotoInteraction: Inserted new interaction for mediaId " + mediaId);
        }
        cursor.close();
    }


    public List<String> getMostViewedThemes() {
        List<String> themes = new ArrayList<>();
        String query = "SELECT t.name, ti.interaction_count " +
                "FROM theme_interaction ti " +
                "JOIN themes t ON t.id = ti.theme_id " +
                "ORDER BY ti.interaction_count DESC " +
                "LIMIT 5";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            themes.add(cursor.getString(0) + " (" + cursor.getInt(1) + " views)");
        }
        cursor.close();
        return themes;
    }

    public List<String> getMostViewedPhotos() {
        List<String> photos = new ArrayList<>();
        String query = "SELECT m.file_path, pi.interaction_count " +
                "FROM photo_interaction pi " +
                "JOIN media_items m ON m.id = pi.media_id " +
                "ORDER BY pi.interaction_count DESC " +
                "LIMIT 5";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d(TAG, "getMostViewedPhotos: Number of rows retrieved: " + cursor.getCount());

        while (cursor.moveToNext()) {
            String filePath = cursor.getString(0);
            int interactionCount = cursor.getInt(1);
            photos.add(filePath + " (" + interactionCount + " views)");
            Log.d(TAG, "getMostViewedPhotos: File path " + filePath + " with " + interactionCount + " views");
        }
        cursor.close();
        return photos;
    }


    public void likePhoto(String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE media_items SET likes = likes + 1 WHERE file_path = ?", new Object[]{filePath});
    }

    public void unlikePhoto(String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE media_items SET likes = likes - 1 WHERE file_path = ?", new Object[]{filePath});
    }

    public int getLikesForPhoto(String filePath) {
        SQLiteDatabase db = this.getReadableDatabase();
        int likes = 0;

        try (Cursor cursor = db.rawQuery("SELECT likes FROM " + TABLE_MEDIA + " WHERE file_path = ?", new String[]{filePath})) {
            if (cursor.moveToFirst()) {
                likes = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting likes for photo: " + e.getMessage());
        }

        return likes;
    }

    public void updateLikes(long mediaId, int increment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE media_items SET likes = likes + ? WHERE id = ?", new Object[]{increment, mediaId});
    }

    public long getMediaIdByFilePath(String filePath) {
        SQLiteDatabase db = this.getReadableDatabase();
        long mediaId = -1;

        Cursor cursor = db.query(TABLE_MEDIA, new String[]{KEY_ID},
                KEY_FILE_PATH + " = ?", new String[]{filePath}, null, null, null);

        if (cursor.moveToFirst()) {
            mediaId = cursor.getLong(0);
        }
        cursor.close();
        return mediaId;
    }

    // Get most liked photos
    public List<String> getMostLikedPhotos(int limit) {
        List<String> photos = new ArrayList<>();
        String query = "SELECT file_path FROM media_items " +
                "WHERE likes > 0 " +
                "ORDER BY likes DESC " +
                "LIMIT ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        while (cursor.moveToNext()) {
            photos.add(cursor.getString(0));
        }
        cursor.close();
        return photos;
    }

    // Get least liked photos (or unliked photos)
    public List<String> getLeastLikedPhotos(int limit) {
        List<String> photos = new ArrayList<>();
        String query = "SELECT file_path FROM media_items " +
                "ORDER BY likes ASC " +
                "LIMIT ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        while (cursor.moveToNext()) {
            photos.add(cursor.getString(0));
        }
        cursor.close();
        return photos;
    }

    // Get likes count for themes
    public List<String> getLikesCountForThemes() {
        List<String> themesWithLikes = new ArrayList<>();
        String query = "SELECT t.name, SUM(m.likes) AS total_likes " +
                "FROM themes t " +
                "JOIN media_theme mt ON t.id = mt.theme_id " +
                "JOIN media_items m ON mt.media_id = m.id " +
                "GROUP BY t.name " +
                "ORDER BY total_likes DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String themeName = cursor.getString(0);
            int totalLikes = cursor.getInt(1);
            themesWithLikes.add(themeName + " (" + totalLikes + " likes)");
        }
        cursor.close();
        return themesWithLikes;
    }

    // Get likes count for photos
    public List<String> getLikesCountForPhotos() {
        List<String> photosWithLikes = new ArrayList<>();
        String query = "SELECT m.file_path, m.likes " +
                "FROM media_items m " +
                "WHERE m.type = 'image' " +
                "ORDER BY m.likes DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String photoPath = cursor.getString(0);
            int likes = cursor.getInt(1);
            photosWithLikes.add(photoPath + " (" + likes + " likes)");
        }
        cursor.close();
        return photosWithLikes;
    }
}