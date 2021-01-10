package com.liangyi.yueting;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
    public String name;//歌曲名
    public String singer;//歌手
    public long size;//歌曲所占空间大小
    public int duration;//歌曲时间长度
    public String path;//歌曲地址
    public long  albumId;//图片id
    public String album;//专辑名称

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long id;//歌曲id
    private Boolean isPlaying = false;//是否正在播放
    public Song(){}
    protected Song(Parcel in) {
        name = in.readString();
        singer = in.readString();
        size = in.readLong();
        duration = in.readInt();
        path = in.readString();
        albumId = in.readLong();
        album=in.readString();
        id = in.readLong();
        byte tmpIsPlaying = in.readByte();
        isPlaying = tmpIsPlaying == 0 ? null : tmpIsPlaying == 1;
    }
    public long getAlbumId()
    {
        return albumId;
    }

    public void setAlbumId(long albumId)
    {
        this.albumId = albumId;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(Boolean playing) {
        isPlaying = playing;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(singer);
        parcel.writeLong(albumId);
        parcel.writeString(album);
        parcel.writeString(path);
        parcel.writeLong(size);
        parcel.writeLong(duration);
        parcel.writeLong(id);
    }
}
