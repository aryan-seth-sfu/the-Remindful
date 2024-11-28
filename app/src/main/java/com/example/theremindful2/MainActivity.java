package com.example.theremindful2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.DialogFragment;
import android.media.MediaPlayer;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private ActivityResultLauncher<Intent> FilePickerLauncher;
    private MusicService musicService;
    private boolean serviceBound = false;
    private static AddRemoveSongs playlistManager;

    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton skipButton;
    private ImageButton backButton;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            serviceBound = true;

            if (playlistManager == null){
                SharedPreferences prefs = getSharedPreferences(getString(R.string.AudioPlayerPreferences), MODE_PRIVATE);
                String playlistJson = prefs.getString(getString(R.string.playlistText), null);
                if (playlistJson != null){
                    playlistManager = new AddRemoveSongs();
                    playlistManager.fromJson(playlistJson);
                }
                else {
                    playlistManager = new AddRemoveSongs();
                    playlistManager.newPlaylist();
                }
            }

            MediaPlayer servicePlayer = musicService.getMediaPlayer();
            if (servicePlayer != null) {
//                updateControl(servicePlayer.isPlaying());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        syncPlaylistManager();

        // Reference to the parent ViewPager2 for horizontal swiping between themes

        ViewPager2 parentViewPager = findViewById(R.id.parentViewPager);
        ParentAdapter parentAdapter = new ParentAdapter(this);

        parentViewPager.setAdapter(parentAdapter);

        parentViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        playButton = findViewById(R.id.playPauseButtonMiniView);
        pauseButton = findViewById(R.id.pause);
        backButton = findViewById(R.id.back);
        skipButton = findViewById(R.id.skip);

        setupListenersMain();


// Start at a middle position for infinite scrolling
        int itemCount = parentAdapter.getItemCount();

// Check if the item count is greater than 0 before proceeding
        if (itemCount > 0) {
            int startPosition = Integer.MAX_VALUE / 2;
            parentViewPager.setCurrentItem(startPosition - (startPosition % itemCount), false);
        } else {
            Log.e(getString(R.string.MainActivity), getString(R.string.ItemCountError));
            // Optionally, handle empty list case or provide fallback behavior
        }


        ImageButton menu = findViewById(R.id.menu_button);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, Menu.class);
//                startActivity(intent);
                Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
                startActivity(intent);
            }
        });
        ImageButton backButton = findViewById(R.id.theback_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
                startActivity(intent);
            }
        });


        Button addImage = findViewById(R.id.addimage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CareGiverImagesSettingsActivity.class);
                startActivity(intent);
            }
        });
//        // Daily Task Feature
//        FloatingActionButton fabTaskBook = findViewById(R.id.fabTaskBook);
//        fabTaskBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Show the TaskDialogFragment
//                DialogFragment taskDialog = new TaskDialogFragment();
//                taskDialog.show(getSupportFragmentManager(), getString(R.string.taskDialogTag));
//            }
//        });
//        FloatingActionButton fabInstruction = findViewById(R.id.instruction);
//        fabInstruction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, instructionPage.class);
//                startActivity(intent);
//
//            }
//        });
        TextView Home = findViewById(R.id.Home);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bindMusicService();


    }

    private void bindMusicService() {
        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setupListenersMain() {
        playButton.setOnClickListener(v -> playButtonStart());
        pauseButton.setOnClickListener(v -> pauseSong());
        skipButton.setOnClickListener(v -> skipCurrentSong());
        backButton.setOnClickListener(v -> backwardsSong());
    }

    ;

    private void backwardsSong() {
        // if service = true and music service isnt empty
        if (serviceBound && musicService != null) {
            // get the media player that was playing in audio activity
            MediaPlayer player = musicService.getMediaPlayer();
//            if (player != null && !player.isPlaying()) {
//                return;
//            }
            if (playlistManager == null){
                Toast.makeText(this, getString(R.string.SongQueueError), Toast.LENGTH_SHORT).show();
                return;
            }
            int currentIndex = musicService.getCurrentSongIndex();
            if (currentIndex != -1 && !playlistManager.allSongs().isEmpty()) {
                if (currentIndex == 0) {
                    currentIndex = playlistManager.allSongs().size() - 1;
                } else {
                    currentIndex = currentIndex - 1;
                }
                musicService.setCurrentSongIndex(currentIndex);
                playCurrentSong(currentIndex);
            }
        }
    }

    private void skipCurrentSong() {
        if (serviceBound && musicService != null) {
            MediaPlayer player = musicService.getMediaPlayer();

            if (playlistManager == null){
                Toast.makeText(this, getString(R.string.SongQueueError), Toast.LENGTH_SHORT).show();
                return;
            }
            int currentIndex = musicService.getCurrentSongIndex();
            if (currentIndex != -1 && !playlistManager.allSongs().isEmpty()) {
                currentIndex = (currentIndex + 1) % playlistManager.allSongs().size();
                musicService.setCurrentSongIndex(currentIndex);
                playCurrentSong(currentIndex);
            }
        }
    }

    private void pauseSong() {
        // if music player exists and service bound = true
        if (serviceBound && musicService != null) {
            // get media player from audio activity
            MediaPlayer player = musicService.getMediaPlayer();
            // if the player isnt empty and is currently playing then pause
            if (player != null && player.isPlaying()) {
                player.pause();
            }
        }
    }

    private void playButtonStart() {
        // if music player exists and service bound = true
        if (serviceBound && musicService != null) {
            // get media player made by audio activity
            MediaPlayer player = musicService.getMediaPlayer();
            // if the player isnt empty and music isnt playing already then start
            if (player != null && !player.isPlaying()) {
                player.start();
            }
        }
    }

    private void playCurrentSong(int index) {
        try {
            // if songs actually exist
            if (!playlistManager.allSongs().isEmpty()) {
                // get the media player from audio activity
                MediaPlayer oldPlayer = musicService.getMediaPlayer();
                if (oldPlayer != null) {
                    oldPlayer.release();
                }

                MediaPlayer newPlayer = new MediaPlayer();
                Song currentSong = playlistManager.allSongs().get(index);
                newPlayer.setDataSource(this, currentSong.getUri());
                newPlayer.prepare();
                newPlayer.start();
                musicService.setMediaPlayer(newPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!serviceBound) {
            bindMusicService();
        }
        if (serviceBound && musicService != null) {
            MediaPlayer player = musicService.getMediaPlayer();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }
}

