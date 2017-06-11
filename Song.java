package pl.edu.pwr.player.musicplayer;

import android.net.Uri;

/**
 * Created by mlyna on 01.06.2017.
 */

public class Song {
    private long id;
    private String title;
    private String artist;
    private Uri image;

    public Song(long songID, String songTitle, String songArtist, Uri image) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        this.image=image;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public Uri getImage(){return image;}
}
