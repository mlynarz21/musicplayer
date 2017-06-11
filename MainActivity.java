package pl.edu.pwr.player.musicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    @BindView(R.id.song_list) ListView songView;
    @BindString(R.string.shuffle_off) String shuffleOFF;
    @BindString(R.string.shuffle_on) String shuffleON;
    private ArrayList<Song> songList;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound=false;
    private boolean isShown = false;

    private MusicController controller;
    private boolean paused=false, playbackPaused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }
        }
        ButterKnife.bind(this);
        initSongList();
        setAdapter();
        setController();
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    private void setAdapter() {
        SongAdapter Adapter = new SongAdapter(this, songList);
        songView.setAdapter(Adapter);
    }

    private void setController() {
        controller = new MusicController(this);
        //set previous and next button listeners
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevSong();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(songView);
        controller.setEnabled(true);
    }

    public void songPicked(View view){
        SongAdapter.ViewHolder holder = (SongAdapter.ViewHolder) view.getTag();
        musicService.setSong(Integer.parseInt(Integer.toString(holder.pos)));

        musicService.playSong();
        if(!isShown) {
            controller.requestFocus();
            controller.show(0);
            isShown=true;
        }
        if(playbackPaused){
//            setController();
            controller.requestFocus();
            playbackPaused=false;
        }
        controller.requestFocus();
    }

    private void playNextSong(){
        musicService.playNext();
        if(playbackPaused){
//            setController();
            controller.requestFocus();
            playbackPaused=false;
        }
        controller.requestFocus();
//        controller.show(0);
//        controller.requestFocus();
    }

    private void playPrevSong(){
        musicService.playPrevios();
        if(playbackPaused){
//            setController();
            controller.requestFocus();
            playbackPaused=false;
        }
        controller.requestFocus();
//        controller.show(0);
//        controller.requestFocus();
    }

    public ArrayList<Song> getSongList() {
        ArrayList<Song> songs = new ArrayList();
        Cursor musicCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (musicCursor == null || !musicCursor.moveToFirst()) {
            return songs;
        }
        int titleColumn = musicCursor.getColumnIndex("title");
        int idColumn = musicCursor.getColumnIndex("_id");
        int artistColumn = musicCursor.getColumnIndex("artist");
        int albumIdColumn = musicCursor.getColumnIndex("album_id");
        do {
            long thisId = musicCursor.getLong(idColumn);
            songs.add(new Song(thisId, musicCursor.getString(titleColumn), musicCursor.getString(artistColumn), getArtwork(thisId)));
        } while (musicCursor.moveToNext());
        return songs;
    }

    public Uri getArtwork(long songId) {
        Uri albumArtUri = null;
        if (songId != -1) {
            String selection = "_id = " + songId + BuildConfig.FLAVOR;
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "album_id"}, selection, null, null);
            if (cursor.moveToFirst()) {
                long albumId = cursor.getLong(cursor.getColumnIndex("album_id"));
                Log.d("Album ID : ", BuildConfig.FLAVOR + albumId);
                albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
            }
            cursor.close();
        }
        return albumArtUri;
    }

    private void initSongList() {
        songList = new ArrayList<>();
        songList = getSongList();
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService !=null && musicBound && musicService.isPlaying())
            return musicService.getPosition();
        else return 0;
    }

    @Override
    public int getDuration() {
        if(musicService !=null && musicBound && musicService.isPlaying())
            return musicService.getDuration();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if(musicService !=null && musicBound)
            return musicService.isPlaying();
        return false;
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicService.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
//            setController();
            controller.requestFocus();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService=null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicService.setShuffle();
                if(musicService.getShuffle())
                    Toast.makeText(getApplicationContext(), shuffleON, Toast.LENGTH_LONG).show();
                else Toast.makeText(getApplicationContext(), shuffleOFF, Toast.LENGTH_LONG).show();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicService =null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

