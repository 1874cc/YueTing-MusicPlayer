package com.liangyi.yueting;

import android.content.Context;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    //点击事件接口
    public interface OnSongItemClickListen{
        void onClick_Song(int position);
    }
    private OnSongItemClickListen listen;

    //写一个点击事件公共的方法
    public void setOnSongItemClickListen(OnSongItemClickListen listen){
        this.listen=listen;
    }

    private List<Song> mSong;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View songView;
        ImageView songImage;
        TextView songName;
        TextView singer;
        TextView songDuration;
        TextView songAlbum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songView=itemView;
            songImage=itemView.findViewById(R.id.ivSongImage);
            songName=itemView.findViewById(R.id.tvSongName);
            singer=itemView.findViewById(R.id.tvSonger);
            songDuration=itemView.findViewById(R.id.tvSongTime);
            songAlbum=itemView.findViewById(R.id.album);
        }
    }

    public SongAdapter(List<Song> songList){
        mSong=songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_songs_list,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song=mSong.get(position);
        holder.singer.setText(song.getSinger());
        holder.songName.setText(song.getName());
        holder.songAlbum.setText(song.getAlbum());
        holder.songDuration.setText(changDuration(song.getDuration()));
        holder.songImage.setImageResource(R.drawable.icon_music);

        /////////////////点击器
        //设置RecyView的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listen!=null)
                    listen.onClick_Song(position);
            }
        });
    }

    public String changDuration(int duration){
        String timeNow = "";
        Integer minute = Integer.valueOf(duration/ 60000);
        Integer seconds = Integer.valueOf(duration% 60000);
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            timeNow += "0";
        }
        timeNow += minute + ":";

        if (second < 10) {
            timeNow += "0";
        }
        timeNow += second;
        return timeNow;
    }
    @Override
    public int getItemCount() {
        return mSong.size();
    }

}
