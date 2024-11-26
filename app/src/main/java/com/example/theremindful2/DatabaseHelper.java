//package com.example.theremindful2;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.net.Uri;
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//    private static final String DATABASE_NAME = "theremindful_db";
//    private static final int DATABASE_VERSION = 1;
//
//    // Table Names
//    private static final String TABLE_MEDIA = "media_items";
//    private static final String TABLE_THEME = "themes";
//    private static final String TABLE_MEDIA_THEME = "media_theme";
//
//    // Common Columns
//    private static final String KEY_ID = "id";
//    private static final String KEY_CREATED_AT = "created_at";
//
//    // Media table columns
//    private static final String KEY_FILE_PATH = "file_path";
//    private static final String KEY_DESCRIPTION = "description";
//    private static final String KEY_TYPE = "type"; // "image" or "audio"
//
//    // Theme table columns
//    private static final String KEY_THEME_NAME = "name";
//
//    // Media_Theme junction table columns
//    private static final String KEY_MEDIA_ID = "media_id";
//    private static final String KEY_THEME_ID = "theme_id";
//
//    // Singleton instance and context
//    private static DatabaseHelper instance;
//    private static Context ctx;
//
//    // Create table SQL queries
//    private static final String CREATE_MEDIA_TABLE =
//            "CREATE TABLE " + TABLE_MEDIA + "("
//                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + KEY_FILE_PATH + " TEXT,"
//                    + KEY_DESCRIPTION + " TEXT,"
//                    + KEY_TYPE + " TEXT,"
//                    + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
//                    + ")";
//
//    private static final String CREATE_THEME_TABLE =
//            "CREATE TABLE " + TABLE_THEME + "("
//                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + KEY_THEME_NAME + " TEXT UNIQUE,"
//                    + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
//                    + ")";
//
//    private static final String CREATE_MEDIA_THEME_TABLE =
//            "CREATE TABLE " + TABLE_MEDIA_THEME + "("
//                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + KEY_MEDIA_ID + " INTEGER,"
//                    + KEY_THEME_ID + " INTEGER,"
//                    + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
//                    + "FOREIGN KEY(" + KEY_MEDIA_ID + ") REFERENCES " + TABLE_MEDIA + "(" + KEY_ID + "),"
//                    + "FOREIGN KEY(" + KEY_THEME_ID + ") REFERENCES " + TABLE_THEME + "(" + KEY_ID + ")"
//                    + ")";
//
//    private DatabaseHelper(Context context) {
//        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
//        ctx = context.getApplicationContext();
//    }
//
//    public static synchronized DatabaseHelper getInstance(Context context) {
//        if (instance == null) {
//            instance = new DatabaseHelper(context.getApplicationContext());
//        }
//        return instance;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_MEDIA_TABLE);
//        db.execSQL(CREATE_THEME_TABLE);
//        db.execSQL(CREATE_MEDIA_THEME_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA_THEME);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THEME);
//        onCreate(db);
//    }
//
//    // Add a new media item
//    public long addMediaItem(String filePath, String description, String type) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_FILE_PATH, filePath);
//        values.put(KEY_DESCRIPTION, description);
//        values.put(KEY_TYPE, type);
//        return db.insert(TABLE_MEDIA, null, values);
//    }
//
//    // Add a new theme
//    public long addTheme(String themeName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_THEME_NAME, themeName);
//        return db.insert(TABLE_THEME, null, values);
//    }
//
//    // Associate media with theme
//    public void addMediaTheme(long mediaId, long themeId) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_MEDIA_ID, mediaId);
//        values.put(KEY_THEME_ID, themeId);
//        db.insert(TABLE_MEDIA_THEME, null, values);
//    }
//
//    // Get all images for a specific theme
//    public List<String> getImagesByTheme(String theme) {
//        List<String> imageList = new ArrayList<>();
//        String selectQuery = "SELECT m." + KEY_FILE_PATH +
//                " FROM " + TABLE_MEDIA + " m" +
//                " INNER JOIN " + TABLE_MEDIA_THEME + " mt ON m." + KEY_ID + " = mt." + KEY_MEDIA_ID +
//                " INNER JOIN " + TABLE_THEME + " t ON mt." + KEY_THEME_ID + " = t." + KEY_ID +
//                " WHERE t." + KEY_THEME_NAME + " = ?" +
//                " AND m." + KEY_TYPE + " = 'image'" +
//                " ORDER BY m." + KEY_CREATED_AT + " DESC";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, new String[]{theme});
//
//        if (cursor.moveToFirst()) {
//            do {
//                imageList.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return imageList;
//    }
//
//    // Get all audio files for a specific theme
//    public List<String> getAudioByTheme(String theme) {
//        List<String> audioList = new ArrayList<>();
//        String selectQuery = "SELECT m." + KEY_FILE_PATH +
//                " FROM " + TABLE_MEDIA + " m" +
//                " INNER JOIN " + TABLE_MEDIA_THEME + " mt ON m." + KEY_ID + " = mt." + KEY_MEDIA_ID +
//                " INNER JOIN " + TABLE_THEME + " t ON mt." + KEY_THEME_ID + " = t." + KEY_ID +
//                " WHERE t." + KEY_THEME_NAME + " = ?" +
//                " AND m." + KEY_TYPE + " = 'audio'" +
//                " ORDER BY m." + KEY_CREATED_AT + " DESC";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, new String[]{theme});
//
//        if (cursor.moveToFirst()) {
//            do {
//                audioList.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return audioList;
//    }
//
//    // Add an Image with multiple themes
//    public boolean addImage(Context context, Uri imageUri, List<String> themes, String description) {
//        if (context == null || imageUri == null || themes == null || themes.isEmpty()) {
//            return false;
//        }
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        boolean success = false;
//        db.beginTransaction();
//
//        try {
//            // First, save the image file
//            File baseDir = new File(context.getFilesDir(), "images");
//            baseDir.mkdirs();
//            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
//            File imageFile = new File(baseDir, fileName);
//            String filePath = imageFile.getAbsolutePath();
//
//            // Copy image file
//            try (InputStream is = context.getContentResolver().openInputStream(imageUri);
//                 OutputStream os = new FileOutputStream(imageFile)) {
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = is.read(buffer)) > 0) {
//                    os.write(buffer, 0, length);
//                }
//            }
//
//            // Add media entry
//            long mediaId = addMediaItem(filePath, description, "image");
//            if (mediaId == -1) {
//                throw new Exception("Failed to insert media entry");
//            }
//
//            // Add or get themes and create associations
//            for (String themeName : themes) {
//                // Get or create theme
//                long themeId;
//                Cursor themeCursor = db.query(TABLE_THEME, new String[]{KEY_ID},
//                        KEY_THEME_NAME + "=?", new String[]{themeName}, null, null, null);
//                if (themeCursor.moveToFirst()) {
//                    themeId = themeCursor.getLong(0);
//                } else {
//                    themeId = addTheme(themeName);
//                }
//                themeCursor.close();
//
//                if (themeId == -1) {
//                    throw new Exception("Failed to process theme: " + themeName);
//                }
//
//                // Create association
//                addMediaTheme(mediaId, themeId);
//            }
//
//            db.setTransactionSuccessful();
//            success = true;
//        } catch (Exception e) {
//            Log.e("DatabaseHelper", "Error in addImage: " + e.getMessage());
//        } finally {
//            db.endTransaction();
//        }
//
//        return success;
//    }
//
//





//    private static final String DATABASE_NAME = "theremindful_db";
//    private static final int DATABASE_VERSION = 1;
//
//    // Table Names
//    private static final String TABLE_MEDIA = "media_items";
//
//    // Common Columns
//    private static final String KEY_ID = "id";
//    private static final String KEY_THEME = "theme";
//    private static final String KEY_FILE_PATH = "file_path";
//    private static final String KEY_DESCRIPTION = "description";
//    private static final String KEY_TYPE = "type"; // "image" or "audio"
//    private static final String KEY_CREATED_AT = "created_at";
//
//    // Singleton instance
//    private static DatabaseHelper instance;
//
//    // Context
//    private static Context ctx;
//
//    // Create table SQL query
//    private static final String CREATE_MEDIA_TABLE =
//            "CREATE TABLE " + TABLE_MEDIA + "("
//                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                    + KEY_THEME + " TEXT,"
//                    + KEY_FILE_PATH + " TEXT,"
//                    + KEY_DESCRIPTION + " TEXT,"
//                    + KEY_TYPE + " TEXT,"
//                    + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
//                    + ")";
//
//    // Private constructor to prevent direct instantiation
//    private DatabaseHelper(Context context) {
//        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
//        ctx = context.getApplicationContext();
//    }
//
//    // Public method to get database instance
//    public static synchronized DatabaseHelper getInstance(Context context) {
//        if (instance == null) {
//            instance = new DatabaseHelper(context.getApplicationContext());
//        }
//        return instance;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        Log.d("Database", "Creating database for first time");
//        db.execSQL(CREATE_MEDIA_TABLE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.d("Database", "Upgrading database from " + oldVersion + " to " + newVersion);
//        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
//        // Create tables again
//        onCreate(db);
//    }
//
//    // Add new media item (image or audio)
//    public long addMediaItem(String theme, String filePath, String description, String type) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_THEME, theme);
//        values.put(KEY_FILE_PATH, filePath);
//        values.put(KEY_DESCRIPTION, description);
//        values.put(KEY_TYPE, type);
//
//        long id = -1;
//        try {
//            id = db.insert(TABLE_MEDIA, null, values);
//            Log.d("Database", "Added new media" +
//                    " item with ID: " + id);
//        } catch (Exception e) {
//            Log.e("Database", "Error adding media item: " + e.getMessage());
//        }
//        return id;
//    }
//
//    // Get all images for a specific theme
//    public List<String> getImagesByTheme(String theme) {
//        List<String> imageList = new ArrayList<>();
//
//        String selectQuery = "SELECT " + KEY_FILE_PATH + " FROM " + TABLE_MEDIA +
//                " WHERE " + KEY_THEME + " = ?" +
//                " AND " + KEY_TYPE + " = 'image'" +
//                " ORDER BY " + KEY_CREATED_AT + " DESC";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, new String[]{theme});
//
//        if (cursor.moveToFirst()) {
//            do {
//                imageList.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return imageList;
//    }
//    // Get all audio files for a specific theme
//    public List<String> getAudioByTheme(String theme) {
//        List<String> audioList = new ArrayList<>();
//
//        String selectQuery = "SELECT " + KEY_FILE_PATH + " FROM " + TABLE_MEDIA +
//                " WHERE " + KEY_THEME + " = ?" +
//                " AND " + KEY_TYPE + " = 'audio'" +
//                " ORDER BY " + KEY_CREATED_AT + " DESC";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, new String[]{theme});
//
//        if (cursor.moveToFirst()) {
//            do {
//                audioList.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return audioList;
//    }
//
//    // Get description for a specific file
//    public String getDescription(String filePath) {
//        String description = null;
//
//        String selectQuery = "SELECT " + KEY_DESCRIPTION + " FROM " + TABLE_MEDIA +
//                " WHERE " + KEY_FILE_PATH + " = ?";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, new String[]{filePath});
//
//        if (cursor.moveToFirst()) {
//            description = cursor.getString(0);
//        }
//
//        cursor.close();
//        return description;
//    }
//
//    // Get all themes
//    public List<String> getAllThemes() {
//        List<String> themes = new ArrayList<>();
//
//        String selectQuery = "SELECT DISTINCT " + KEY_THEME + " FROM " + TABLE_MEDIA +
//                " ORDER BY " + KEY_THEME + " ASC";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                themes.add(cursor.getString(0));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        return themes;
//    }
//
//    // Update description
//    public int updateDescription(String filePath, String newDescription) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_DESCRIPTION, newDescription);
//
//        return db.update(TABLE_MEDIA, values, KEY_FILE_PATH + " = ?",
//                new String[]{filePath});
//    }
//
//    // Delete media item
//    public void deleteMediaItem(String filePath) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_MEDIA, KEY_FILE_PATH + " = ?", new String[]{filePath});
//    }
//
//    // Get all themes with images
//    public List<Theme> getAllThemesWithImages() {
//        List<Theme> themeList = new ArrayList<>();
//
//        // First get all unique themes
//        String selectThemesQuery = "SELECT DISTINCT " + KEY_THEME + " FROM " + TABLE_MEDIA +
//                " ORDER BY " + KEY_THEME + " ASC";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor themeCursor = db.rawQuery(selectThemesQuery, null);
//
//        if (themeCursor.moveToFirst()) {
//            do {
//                String themeName = themeCursor.getString(0);
//
//                // For each theme, get its images
//                List<String> photos = getImagesByTheme(themeName);
//
//                // Create Theme object and add to list
//                Theme theme = new Theme(themeName, photos);
//                themeList.add(theme);
//
//            } while (themeCursor.moveToNext());
//        }
//
//        themeCursor.close();
//        return themeList;
//    }
//
//    // Get all themes that contain a specific image
//    public List<Theme> filterThemesByImagePath(String imagePath) {
//        List<Theme> filteredThemes = new ArrayList<>();
//
//        // Get all themes that contain this image
//        String selectQuery =
//                "SELECT DISTINCT t." + KEY_THEME +
//                        " FROM " + TABLE_MEDIA + " m" +
//                        " WHERE m." + KEY_FILE_PATH + " = ?" +
//                        " AND m." + KEY_TYPE + " = 'image'";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, new String[]{imagePath});
//
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//                    String themeName = cursor.getString(0);
//                    // For each theme that has this image, get all images in that theme
//                    List<String> themeImages = getImagesByTheme(themeName);
//                    // Create Theme object and add to filtered list
//                    filteredThemes.add(new Theme(themeName, themeImages));
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            Log.e("DatabaseHelper", "Error filtering themes: " + e.getMessage());
//        } finally {
//            cursor.close();
//        }
//
//        return filteredThemes;
//    }
//
//    // Remove Image from a theme
//    public boolean removeImageFromTheme(String imagePath, String themeName) {
//        if (imagePath == null || themeName == null) {
//            Log.e("DatabaseHelper", "Invalid image path or theme name");
//            return false;
//        }
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        boolean success = false;
//        db.beginTransaction();
//
//        try {
//            // First check if image exists in this theme
//            Cursor checkCursor = db.query(TABLE_MEDIA,
//                    new String[]{KEY_ID},
//                    KEY_FILE_PATH + " = ? AND " + KEY_THEME + " = ?",
//                    new String[]{imagePath, themeName},
//                    null, null, null);
//
//            if (!checkCursor.moveToFirst()) {
//                Log.d("DatabaseHelper", "Image not found in theme: " + themeName);
//                checkCursor.close();
//                return false;
//            }
//            checkCursor.close();
//
//            // Delete the record
//            int rowsAffected = db.delete(TABLE_MEDIA,
//                    KEY_FILE_PATH + " = ? AND " + KEY_THEME + " = ?",
//                    new String[]{imagePath, themeName});
//
//            if (rowsAffected > 0) {
//                // Check if image exists in other themes
//                Cursor remainingCursor = db.query(TABLE_MEDIA,
//                        new String[]{KEY_FILE_PATH},
//                        KEY_FILE_PATH + " = ?",
//                        new String[]{imagePath},
//                        null, null, null);
//
//                if (!remainingCursor.moveToFirst()) {
//                    // Image doesn't exist in any theme, delete the file
//                    File imageFile = new File(imagePath);
//                    if (imageFile.exists()) {
//                        boolean fileDeleted = imageFile.delete();
//                        Log.d("DatabaseHelper", "Image file deleted: " + fileDeleted);
//                    }
//                }
//                remainingCursor.close();
//
//                // Get remaining images count in theme
//                Cursor themeImagesCursor = db.query(TABLE_MEDIA,
//                        new String[]{"COUNT(*)"},
//                        KEY_THEME + " = ?",
//                        new String[]{themeName},
//                        null, null, null);
//
//                if (themeImagesCursor.moveToFirst()) {
//                    int remainingImages = themeImagesCursor.getInt(0);
//                    Log.d("DatabaseHelper", "Theme " + themeName +
//                            " has " + remainingImages + " images remaining");
//                }
//                themeImagesCursor.close();
//
//                success = true;
//            }
//
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.e("DatabaseHelper", "Error in removeImageFromTheme: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            db.endTransaction();
//        }
//
//        return success;
//    }
//
//    // Add an Image with multiple tags
//    public boolean addImage(Context context, Uri newImageUri, List<String> selectedTags, String description) {
//        if (context == null || newImageUri == null || selectedTags == null || selectedTags.isEmpty()) {
//            Log.e("DatabaseHelper", "Invalid parameters provided to addImage");
//            return false;
//        }
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        boolean success = false;
//        String timestamp = String.valueOf(System.currentTimeMillis());
//
//        db.beginTransaction();
//
//        try {
//            // For each theme, create a separate copy of the image
//            int successfulInserts = 0;
//
//            for (String themeName : selectedTags) {
//                // Create theme-specific directory
//                File themeDir = new File(context.getFilesDir(), "images/" + themeName);
//                themeDir.mkdirs();
//
//                // Create unique filename for this theme
//                String fileName = "img_" + timestamp + "_" + themeName + ".jpg";
//                File destinationFile = new File(themeDir, fileName);
//                String filePath = destinationFile.getAbsolutePath();
//
//                // Copy image file for this theme
//                try (InputStream is = context.getContentResolver().openInputStream(newImageUri);
//                     OutputStream os = new FileOutputStream(destinationFile)) {
//
//                    if (is == null) {
//                        Log.e("DatabaseHelper", "Failed to open input stream for theme: " + themeName);
//                        continue;
//                    }
//
//                    // Copy file
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = is.read(buffer)) > 0) {
//                        os.write(buffer, 0, length);
//                    }
//                    os.flush();
//
//                    Log.d("DatabaseHelper", "Image saved for theme " + themeName + " at: " + filePath);
//
//                    // Add database entry for this theme
//                    ContentValues values = new ContentValues();
//                    values.put(KEY_THEME, themeName);
//                    values.put(KEY_FILE_PATH, filePath);
//                    values.put(KEY_DESCRIPTION, description);
//                    values.put(KEY_TYPE, "image");
//                    values.put(KEY_CREATED_AT, timestamp);
//
//                    long id = db.insert(TABLE_MEDIA, null, values);
//
//                    if (id != -1) {
//                        successfulInserts++;
//                        Log.d("DatabaseHelper", "Added to theme: " + themeName + " with ID: " + id);
//                    } else {
//                        Log.e("DatabaseHelper", "Failed to add database entry for theme: " + themeName);
//                        // If database insert fails, delete the copied file
//                        destinationFile.delete();
//                    }
//
//                } catch (IOException e) {
//                    Log.e("DatabaseHelper", "Error saving image for theme " + themeName + ": " + e.getMessage());
//                    // Clean up file if copy failed
//                    if (destinationFile.exists()) {
//                        destinationFile.delete();
//                    }
//                }
//            }
//
//            // Check if all operations were successful
//            if (successfulInserts == selectedTags.size()) {
//                db.setTransactionSuccessful();
//                success = true;
//                Log.d("DatabaseHelper", "Successfully added image to all " + successfulInserts + " themes");
//            } else {
//                Log.w("DatabaseHelper", "Only " + successfulInserts + " out of " +
//                        selectedTags.size() + " theme additions were successful");
//            }
//
//        } catch (Exception e) {
//            Log.e("DatabaseHelper", "Error in addImage: " + e.getMessage());
//            e.printStackTrace();
//
//            // If any error occurs, we'll let the transaction rollback
//            // and cleanup will happen in finally block
//        } finally {
//            db.endTransaction();
//
//            // If overall operation failed, clean up any created files
//            if (!success) {
//                for (String themeName : selectedTags) {
//                    String fileName = "img_" + timestamp + "_" + themeName + ".jpg";
//                    File themeDir = new File(context.getFilesDir(), "images/" + themeName);
//                    File imageFile = new File(themeDir, fileName);
//                    if (imageFile.exists()) {
//                        imageFile.delete();
//                    }
//                }
//            }
//        }
//
//        return success;
//    }
//
//
//}


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
            "CREATE TABLE " + TABLE_MEDIA + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_FILE_PATH + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_TYPE + " TEXT,"
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE media_items ADD COLUMN likes INTEGER DEFAULT 0;");
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA_THEME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THEME);
        db.execSQL("DROP TABLE IF EXISTS theme_interaction");
        db.execSQL("DROP TABLE IF EXISTS photo_interaction");
        onCreate(db);
    }

    // Add a new media item
    public long addMediaItem(String filePath, String description, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FILE_PATH, filePath);
        values.put(KEY_DESCRIPTION, description);
        values.put(KEY_TYPE, type);
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
        } else {
            // Insert new interaction record
            ContentValues values = new ContentValues();
            values.put("media_id", mediaId);
            values.put("interaction_count", 1);
            db.insert("photo_interaction", null, values);
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

        while (cursor.moveToNext()) {
            photos.add(cursor.getString(0) + " (" + cursor.getInt(1) + " views)");
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
        Cursor cursor = db.rawQuery("SELECT likes FROM media_items WHERE file_path = ?", new String[]{filePath});
        int likes = 0;
        if (cursor.moveToFirst()) {
            likes = cursor.getInt(0);
        }
        cursor.close();
        return likes;
    }

    public void updateLikes(long mediaId, int increment) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE media_items SET likes = likes + ? WHERE id = ?", new Object[]{increment, mediaId});
    }


    // Theme class to hold theme information
//    public static class Theme {
//        private String name;
//        private List<String> mediaPaths;
//
//        public Theme(String name, List<String> mediaPaths) {
//            this.name = name;
//            this.mediaPaths = mediaPaths;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public List<String> getMediaPaths() {
//            return mediaPaths;
//        }
//    }
}