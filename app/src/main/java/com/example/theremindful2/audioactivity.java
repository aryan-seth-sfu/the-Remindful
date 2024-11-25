package com.example.theremindful2;

import android.os.Bundle;
import android.media.MediaPlayer;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.net.Uri;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.SeekBar;

import java.io.File;
import java.io.IOException;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class audioactivity extends AppCompatActivity {
    private MusicService musicService;
    // service bound is used to track if service is connected or not
    // true would be its connected to background music service false is not
    private boolean serviceBound = false;
    // manage playlist of songs
    private static AddRemoveSongs playlistManage;
    private static MediaPlayer mediaPlayer;
    private static int currentSongIndex = 0;
    private static int currentPlaybackPosition = 0;
    public MediaManager MediaManager;

    // constants used to save app state
    // names the preference file created where everything will be saved into
    private static final String PREFERENCE_NAME = "AudioPlayerPreference";
//    private static final String KEY_CURRENT_SONG_INDEX = "currentSongIndex";
    private static final String currentPlaybackPos = "currentPosition";
    private static final String thePlaylist = "playlist";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            if (mediaPlayer.isPlaying()) {
            // when the service is connected we want to bind and connect to music service
            // so we can use its funcs etc
                MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
//            serviceBound = true;
            // get
                musicService = binder.getService();
                // set service connected to be true
                serviceBound = true;
//            }
//            mediaPlayerinBackground(mediaPlayer);
            // gets the  media player from music service func

                MediaPlayer serviceMediaPlayer = musicService.getMediaPlayer();
                // checks if sucessfully retrieved
            if (serviceMediaPlayer != null) {
                try {
                    // if the media player is being played in background right now then update UI
                    // so everything is synced and updated
                    boolean isPlaying = serviceMediaPlayer.isPlaying();
//                    if (!isPlaying){
//                  Toast.makeText(this, "Music not playing right now added!", Toast.LENGTH_SHORT).show();
//                    }
//                    else
                    if (isPlaying) {

                        mediaPlayer = serviceMediaPlayer;
                        updateUIFromMediaPlayer();
                    }
                } catch (IllegalStateException e) {
                    // if not null we can set use it and reset it to service
                    if (mediaPlayer != null) {
                        musicService.setMediaPlayer(mediaPlayer);
                    }
                }
            }
            // if service player null check if mediaplayer null and set
            else if (mediaPlayer != null) {
                musicService.setMediaPlayer(mediaPlayer);
            }
        }

        // if service disconnected then set to false
                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        serviceBound = false;
                    }
    };

    Uri audio;
    private Button audioButton;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton skipButton;
    private ImageButton backButton;
    private Uri audioUri;
    private boolean isPlaying = false;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView totalTime;
    private int seekBarProgress = 0;
//    MediaManager mm = new MediaManager(getApplicationContext());

//    List<String> themes;



    // ActivityResultLauncher to start a launcer that gets a result (audio file) from it
    // register for activity result will be a launcher that starts activity and deals with audio file
    // new activitiy result contracts... starts activity fetches result
    private ActivityResultLauncher<Intent> audioPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), audioReturned -> {
                // if properly fetched the file then set the datafile and save it
                if (audioReturned.getResultCode() == RESULT_OK) {

                    List<String> themes = new ArrayList<>();
                    themes.add("audioPlaylist");

                    Intent datafile = audioReturned.getData();
                    if (datafile != null) {

                        audioUri = datafile.getData();

                        // get persistence permission so files can still be playing when i exit audio activity and return
                        getContentResolver().takePersistableUriPermission(audioUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Song newSong = new Song(audioUri);
                        playlistManage.addSong(newSong);
                        MediaManager mm = new MediaManager(getApplicationContext());
                        mm.addAudio(audioUri, themes , null);
//                        playCurrentSong();
//                        currentSongIndex++;

                        // after adding a new song save state and keep updated
                        saveState();
                        Toast.makeText(this, "Song added!", Toast.LENGTH_SHORT).show();
                         }
                    }
             }
    );


    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private String AUDIO_PLAYLIST_NAME = "audioPlaylist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


//        themes.add("audioPlaylist");
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audioactivity);
        //
        setUI();

        // if playlist doesnt exist create a new one
        if (playlistManage == null) {
            playlistManage = new AddRemoveSongs();
            playlistManage.newPlaylist();
        }

        try {

        loadState();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // startUpdatingUI();
        // if (!MediaPlayer.isPlaying()){


        setupListeners();
        setupSeekBar();
// }
        // Bind to music service and background service
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(serviceIntent);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadState() throws JSONException {
        // loads the saved playback position
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        // if playback pos not found it sets to 0
        currentPlaybackPosition = prefs.getInt(currentPlaybackPos, 0);

        // Load playlist json makes it readable
        String playlistJson = prefs.getString(thePlaylist, null);
        MediaManager mm = new MediaManager(getApplicationContext());

        List<String> lst = mm.getAudioForTheme(AUDIO_PLAYLIST_NAME);
//        JSONArray temp = new JSONArray(playlistJson);

        // if playlist is not empty and playlist manage doesnt exist then create a new playlist manage and update

        playlistManage = new AddRemoveSongs();
        for (int i = 0; i < lst.size() ; i++){
            File f = new File(lst.get(i));
            Uri uri  = Uri.fromFile(f);
            playlistManage.addSong(new Song(uri));
        }

        if (playlistJson != null && playlistManage == null) {
            playlistManage = new AddRemoveSongs();
            playlistManage.fromJson(playlistJson);
        }
        // if playlist is empty then create a new playlist instance
        else if (playlistManage == null) {
            playlistManage = new AddRemoveSongs();
            playlistManage.newPlaylist();
        }

        // if service bound is true and its connected and music service isnt empty then update the current song index
        if (serviceBound && musicService != null) {
            int serviceIndex = musicService.getCurrentSongIndex();
            // ensure not out of bounds and then set
            if (serviceIndex != -1) {
                currentSongIndex = serviceIndex;

            }
        }
    }

    // save state over activities
    private void saveState() {
        // in order to alter shared preferences aka the information we have saved we need to call the editor

        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit();
        // if media player not empty update the playback pos and save it
        if (mediaPlayer != null) {
            editor.putInt(currentPlaybackPos, mediaPlayer.getCurrentPosition());
        }

        // saves the current playlist
        if (playlistManage != null) {
            editor.putString(thePlaylist, playlistManage.toJson());
        }
        // applies the changes within editor
        editor.apply();


        // aryan code
//        MediaManager mm = new MediaManager(getApplicationContext());


    }


    // we need on stop to keep the lifecycle when no longer in activity
    @Override
    protected void onStop() {
        super.onStop();
        // save state before exiting the activity
        saveState();
        if (mediaPlayer != null) {
            currentPlaybackPosition = mediaPlayer.getCurrentPosition();
        }
    }

    // set everything to the id it should have so buttons are linked properly
    private void setUI() {
        audioButton = findViewById(R.id.bt_audio);
        playButton = findViewById(R.id.playicon);
        pauseButton = findViewById(R.id.pause);
        skipButton = findViewById(R.id.skip);
        backButton = findViewById(R.id.backwards);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
    }

    // call all funcs when corresponding buttons are clicked
    private void setupListeners() {
        audioButton.setOnClickListener(v -> openAudioPicker());
        playButton.setOnClickListener(v -> playCurrentSong());
        pauseButton.setOnClickListener(v -> pauseCurrentSong());
        skipButton.setOnClickListener(v -> skipSong());
        backButton.setOnClickListener(v -> backSong());
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // if the user is the one whos changing the seekbar and media player isnt empty we want to update the seek bar
                if (fromUser && mediaPlayer != null) {
                    currentPlaybackPosition = progress;
                    // update the progress to what the user selects and then update the seekbar time
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatDuration(progress));
                }
            }

            // when user starts moving seekbar
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopUpdatingSeekBar();
            }

            // when the user stops moving seekbar
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    startUpdatingSeekBar();
                   }
                }
        });
    }


    private void updateUIFromMediaPlayer() {
        // check if media player is not empty
        if (mediaPlayer != null) {
            try {
                // set seekbar to match the song
                seekBar.setMax(mediaPlayer.getDuration());

                seekBar.setProgress(mediaPlayer.getCurrentPosition());

                currentTime.setText(formatDuration(mediaPlayer.getCurrentPosition()));

                totalTime.setText(formatDuration(mediaPlayer.getDuration()));
                // if the music is playing in the background (switched activities etc) update seekbar to match
                if (mediaPlayer.isPlaying()) {
                    startUpdatingSeekBar();
                }
            } catch (IllegalStateException e) {

                stopUpdatingSeekBar();
            }
          }
//        else {
//            mediaPlayer.setMediaPlayer(player);
//
//        }
        }
    // start intent for google drive and open it
    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        // get permissions and ensure you can still access file over activity switches
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        audioPickerLauncher.launch(Intent.createChooser(intent, "Pick audio"));
    }

// play the song selected by user
    private void playCurrentSong() {
        // if playlist is empty then tell user
        if (playlistManage.allSongs().isEmpty()) {
            Toast.makeText(this, "No songs available. Add more songs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer != null) {
                // if music already playing dont do anything
                if (mediaPlayer.isPlaying() ) {

                    return;
                }
                // make a new instance of media player
                mediaPlayer.release();

                mediaPlayer = null;
            }

            // get index from service
            if (serviceBound && musicService != null) {

                int serviceIndex = musicService.getCurrentSongIndex();

                if (serviceIndex != -1) {
                    currentSongIndex = serviceIndex;
                }
             }

            mediaPlayer = new MediaPlayer();
            Song currentSong = playlistManage.allSongs().get(currentSongIndex);
            // ensure permissions since then when in different activities can lose file access
            try {
                getContentResolver().takePersistableUriPermission(currentSong.getUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
            }

            // get the uri
            mediaPlayer.setDataSource(this, currentSong.getUri());
            // use in built media player func to prepare
            mediaPlayer.prepare();
            mediaPlayer.seekTo(currentPlaybackPosition);

            mediaPlayer.setOnCompletionListener(mp -> {
                currentPlaybackPosition = 0;
                nextSong();
             });

            mediaPlayer.start();

            if (serviceBound) {
                musicService.setMediaPlayer(mediaPlayer);
                musicService.setCurrentSongIndex(currentSongIndex);
            }

            updateUIFromMediaPlayer();

        } catch (IOException e) {
            Toast.makeText(this, "Error playing song: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // pause the song your on when user clicks on pause button
    private void pauseCurrentSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            currentPlaybackPosition = mediaPlayer.getCurrentPosition();

            mediaPlayer.pause();

            stopUpdatingSeekBar();
        }
    }
// skip song when user presses skip button
    private void skipSong() {
        // check if playlist is empty
        if (!playlistManage.isEmpty()) {
            // check if media player is empty
            if (mediaPlayer != null) {

                mediaPlayer.release();

                mediaPlayer = null;
            }
            // set the song index to be the next one and loop around if u hit end of playlist size
            currentSongIndex = (currentSongIndex + 1) % playlistManage.allSongs().size();
            // if service is connected set properly
            if (serviceBound) {
                musicService.setCurrentSongIndex(currentSongIndex);
            }
            currentPlaybackPosition = 0;
            // call the function to start the song once everything is set
            playCurrentSong();
        }
    }

    private void backSong() {
        // check if playlist is empty
        if (!playlistManage.isEmpty()) {
            // check if media player is empty
            if (mediaPlayer != null) {

                  mediaPlayer.release();

                mediaPlayer = null;
            }
            // if it hits the first song and we want to go back then we loop it so it hits the back of the playlist
            if (currentSongIndex == 0) {

                currentSongIndex = playlistManage.allSongs().size() - 1;
            }
            // if not first song then we just regularly change index
            else {
                currentSongIndex = currentSongIndex - 1;
            }
            // if service connected then we set
            if (serviceBound) {
                musicService.setCurrentSongIndex(currentSongIndex);
            }
            currentPlaybackPosition = 0;
            // call func to play song
            playCurrentSong();
        }
    }

    private void nextSong() {
        currentSongIndex = (currentSongIndex + 1) % playlistManage.allSongs().size();
        // if connected to service then set
        if (serviceBound) {
            musicService.setCurrentSongIndex(currentSongIndex);
        }
        // if not empty start new state
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPlaybackPosition = 0;
        playCurrentSong();
    }

    private void startUpdatingSeekBar() {
        // make the seekbar stop so we can change it
        stopUpdatingSeekBar();

        updateSeekBar = new Runnable() {
            @Override
            public void run() {

              if (mediaPlayer != null) {
               try {
                   // if music is playing update seekbar
                  if (mediaPlayer.isPlaying()) {
                      seekBar.setProgress(mediaPlayer.getCurrentPosition());
                       currentTime.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                        handler.postDelayed(this, 1000);
                        }
                    } catch (IllegalStateException e) {
                        stopUpdatingSeekBar();
                    }
                 }
            }
        };
        // handler used to continously update seekbar in ui
        handler.post(updateSeekBar);
    }

    // stop the seekbar from continously changing
    private void stopUpdatingSeekBar() {
        handler.removeCallbacks(updateSeekBar);
    }

    // format duration being presented for seekbar
    private String formatDuration(int duration) {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if service connected unconnect it
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        stopUpdatingSeekBar();
    }

    // saves current playback position and stops seekbar
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            currentPlaybackPosition = mediaPlayer.getCurrentPosition();
        }
        stopUpdatingSeekBar();
    }

    // ensures the ui is updated
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            // update ui
            try {
                updateUIFromMediaPlayer();
                if (mediaPlayer.isPlaying()) {
                    startUpdatingSeekBar();
                }
            } catch (IllegalStateException e) {
                }
        }
    }
}





// working as well^^