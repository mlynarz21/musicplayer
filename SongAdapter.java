package pl.edu.pwr.player.musicplayer;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mlyna on 01.06.2017.
 */

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;

    private LayoutInflater songInf;
    public SongAdapter(Context c, ArrayList<Song> theSongs){
        songs=theSongs;
        songInf=LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    static class ViewHolder {
        TextView songView;
        TextView artistView;
        ImageView imageView;
        int pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = songInf.inflate(R.layout.song_layout, parent,false);
            holder = new ViewHolder();
            holder.songView = (TextView)convertView.findViewById(R.id.song_title);
            holder.artistView = (TextView)convertView.findViewById(R.id.song_artist);
            holder.imageView = (ImageView) convertView.findViewById(R.id.song_img);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Song currSong = songs.get(position);
        //get title and artist strings
        holder.songView.setText(currSong.getTitle());
        holder.artistView.setText(currSong.getArtist());
        holder.imageView.setImageURI(currSong.getImage());
        holder.pos = position;

        return convertView;
    }
}



