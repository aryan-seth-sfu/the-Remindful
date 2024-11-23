package com.example.theremindful2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.theremindful2.data.AppDatabase;
import com.example.theremindful2.data.Audio;
import com.example.theremindful2.data.AudioRepository;

import java.io.IOException;
import java.util.Locale;

public class audioactivity extends AppCompatActivity {
    private MusicService musicService;
    private boolean serviceBound = false;
    private AddRemoveSongs playlistManager;
    private AudioRepository repository;
    private static MediaPlayer mediaPlayer;
    private static int currentSongIndex = 0;
    private static int currentPlaybackPosition = 0;

    private static final String PREFERENCE_NAME = "AudioPlayerPreference";
    private static final String CURRENT_PLAYBACK_POS = "currentPosition";

    private Button audioButton;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton skipButton;
    private ImageButton backButton;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView totalTime;

    private final Handler handler = new Handler();
    private Runnable updateSeekBar;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            serviceBound = true;

            MediaPlayer serviceMediaPlayer = musicService.getMediaPlayer();
            if (serviceMediaPlayer != null) {
                try {
                    boolean isPlaying = serviceMediaPlayer.isPlaying();
                    if (isPlaying) {
                        mediaPlayer = serviceMediaPlayer;
                        updateUIFromMediaPlayer();
                    }
                } catch (IllegalStateException e) {
                    if (mediaPlayer != null) {
                        musicService.setMediaPlayer(mediaPlayer);
                    }
                }
            } else if (mediaPlayer != null) {
                musicService.setMediaPlayer(mediaPlayer);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private final ActivityResultLauncher<Intent> audioPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri audioUri = result.getData().getData();
                    if (audioUri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    audioUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                            playlistManager.addSong(audioUri, this);
                            Toast.makeText(this, "Song added!", Toast.LENGTH_SHORT).show();
                        } catch (SecurityException e) {
                            Toast.makeText(this, "Error adding song", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audioactivity);

        // Initialize repository and playlist manager
        AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
        repository = new AudioRepository(database);
        playlistManager = new AddRemoveSongs(repository);

        setUI();
        loadState();
        setupListeners();
        setupSeekBar();

        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(serviceIntent);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadState() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        currentPlaybackPosition = prefs.getInt(CURRENT_PLAYBACK_POS, 0);

        if (serviceBound && musicService != null) {
            int serviceIndex = musicService.getCurrentSongIndex();
            if (serviceIndex != -1) {
                currentSongIndex = serviceIndex;
            }
        }
    }

    private void saveState() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit();
        if (mediaPlayer != null) {
            editor.putInt(CURRENT_PLAYBACK_POS, mediaPlayer.getCurrentPosition());
        }
        editor.apply();
    }

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

    private void setupListeners() {
        audioButton.setOnClickListener(v -> openAudioPicker());
        playButton.setOnClickListener(v -> playCurrentSong());
        pauseButton.setOnClickListener(v -> pauseCurrentSong());
        skipButton.setOnClickListener(v -> skipSong());
        backButton.setOnClickListener(v -> backSong());
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        audioPickerLauncher.launch(Intent.createChooser(intent, "Pick audio"));
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    currentPlaybackPosition = progress;
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopUpdatingSeekBar();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    startUpdatingSeekBar();
                }
            }
        });
    }

    private void playCurrentSong() {
        if (playlistManager.isEmpty()) {
            Toast.makeText(this, "No songs available. Add more songs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    return;
                }
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            Audio currentSong = playlistManager.allSongs().get(currentSongIndex);
            Uri uri = Uri.parse(currentSong.getAudioPath());

            try {
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (SecurityException e) {
                // Handle permission error
            }

            mediaPlayer.setDataSource(this, uri);
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
            Toast.makeText(this, "Error playing song: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseCurrentSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPlaybackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            stopUpdatingSeekBar();
        }
    }

    private void skipSong() {
        if (!playlistManager.isEmpty()) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Audio nextSong = playlistManager.getNextSong();
            if (nextSong != null) {
                if (serviceBound) {
                    musicService.setCurrentSongIndex(playlistManager.getCurrentIndex());
                }
                currentPlaybackPosition = 0;
                playCurrentSong();
            }
        }
    }

    private void backSong() {
        if (!playlistManager.isEmpty()) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Audio previousSong = playlistManager.getPreviousSong();
            if (previousSong != null) {
                if (serviceBound) {
                    musicService.setCurrentSongIndex(playlistManager.getCurrentIndex());
                }
                currentPlaybackPosition = 0;
                playCurrentSong();
            }
        }
    }

    private void nextSong() {
        currentSongIndex = (currentSongIndex + 1) % playlistManager.allSongs().size();
        if (serviceBound) {
            musicService.setCurrentSongIndex(currentSongIndex);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentPlaybackPosition = 0;
        playCurrentSong();
    }

    private void updateUIFromMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                currentTime.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                totalTime.setText(formatDuration(mediaPlayer.getDuration()));
                if (mediaPlayer.isPlaying()) {
                    startUpdatingSeekBar();
                }
            } catch (IllegalStateException e) {
                stopUpdatingSeekBar();
            }
        }
    }

    private void startUpdatingSeekBar() {
        stopUpdatingSeekBar();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    try {
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
        handler.post(updateSeekBar);
    }

    private void stopUpdatingSeekBar() {
        handler.removeCallbacks(updateSeekBar);
    }

    private String formatDuration(int duration) {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        stopUpdatingSeekBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveState();
        if (mediaPlayer != null) {
            currentPlaybackPosition = mediaPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            currentPlaybackPosition = mediaPlayer.getCurrentPosition();
        }
        stopUpdatingSeekBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
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