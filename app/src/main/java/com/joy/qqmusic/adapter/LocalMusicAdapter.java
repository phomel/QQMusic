package com.joy.qqmusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joy.qqmusic.R;
import com.joy.qqmusic.bean.Music;

import java.util.List;

/**
 * Created by Administrator on 2016/10/6.
 */
public class LocalMusicAdapter extends BaseAdapter {

    private Context context;
    private List<Music> list;

    public LocalMusicAdapter(Context context, List<Music> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_list_local_music, null);
            holder = new ViewHolder();
            holder.isPlaying = convertView.findViewById(R.id.iv_music_is_playing);
            holder.title = (TextView) convertView.findViewById(R.id.tv_item_music_title);
            holder.artist = (TextView) convertView.findViewById(R.id.tv_item_music_artist);
            holder.duration = (TextView) convertView.findViewById(R.id.tv_item_music_duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Music music = list.get(position);
        holder.title.setText(music.getTitle());
        holder.artist.setText(music.getArtist());
        holder.duration.setText(music.getDurStr());
        holder.isPlaying.setTag(position);
        if (music.isPlaying()) {
            holder.isPlaying.setVisibility(View.VISIBLE);
        } else  {
            holder.isPlaying.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        View isPlaying;
        TextView title, artist, duration;
    }
}
