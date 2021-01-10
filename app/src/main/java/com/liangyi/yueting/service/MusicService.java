package com.liangyi.yueting.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.liangyi.yueting.IndexActivity;
import com.liangyi.yueting.PlayDetailActivity;
import com.liangyi.yueting.R;
import com.liangyi.yueting.ScanMusicUtils;
import com.liangyi.yueting.Song;

import java.io.IOException;
import java.util.List;

//music服务，起到播放音乐的功能
//MusicService刚刚启动的时候就注册了一个广播，为的是让它在歌曲播完进行下一首播放
public class MusicService extends Service {
    public static MediaPlayer mlastPlayer;//当前歌曲Media
    public static int mPosition;//当前歌曲下标
    private int position;
    private String path=null;
    private  MediaPlayer player;
    private Song song;
    private List<Song> songlist;
    private Context context;

    private String TAG="MusicService";
    private RemoteViews remoteView;
    private Notification notification;//通知
    private String notificationChannelId="playMusic";//通知渠道id
    private int notifyId=1;

    public static String ACTION="to_service";
    public static String MAIN_UPDATE_UI="index_activity_ui";
    public static String KEY_USR_ACTION = "key_usr_action";
    public static final int ACTION_PRE = 0, ACTION_PLAY_PAUSE = 1, ACTION_NEXT = 2;
    public static String KEY_MAIN_ACTIVITY_UI_BTN = "index_activity_ui_btn_key";
    public static String KEY_MAIN_ACTIVITY_UI_TEXT = "index_activity_ui_text_key";
    public static final int  VAL_UPDATE_UI_PLAY = 1,VAL_UPDATE_UI_PAUSE =2;
    NotificationCompat.Builder mBuilder;//notification建造者
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new musicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        songlist= ScanMusicUtils.getMusicData(context);//获取音乐列表
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(receiver,intentFilter);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotificationBar();
        Bundle bundle=intent.getExtras();
        position=bundle.getInt("position");
        if(mlastPlayer==null||mPosition!=position){
            Log.d("播放页", "onStartCommand: "+"mPosition"+mPosition+"   position"+position);
            prepare();
        }else{
            player=mlastPlayer;
        }
        return super.onStartCommand(intent, flags, startId);
    }
    //初始化Notification通知
    private void initNotificationBar(){
        //检查当前Android版本，8.0以上需设置通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "通知栏播放";
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel mChannel = new NotificationChannel(notificationChannelId, name, importance);
            mChannel.setDescription(description);
            mChannel.setLightColor(Color.RED);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
        mBuilder=new NotificationCompat.Builder(this,notificationChannelId);
        //获取当前歌曲信息
        Log.d("通知栏播放页init", "updateNotification: 更新状态栏歌曲新息");
        String songName=songlist.get(MusicService.mPosition).getName();
        String songSinger=songlist.get(MusicService.mPosition).getSinger();
        //获取通知栏歌曲控制器
        remoteView=new RemoteViews(getPackageName(),R.layout.notification);
        remoteView.setTextViewText(R.id.notification_title,songName+" - "+songSinger);
        loadingCover(songlist.get(MusicService.mPosition).getPath());
        /////控制按钮

        //设置通知栏样式
        mBuilder.setWhen(System.currentTimeMillis())
                .setContentTitle("悦听~")
                .setContent(remoteView)
                .setSmallIcon(R.drawable.icon_music)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon_music));
        notification=mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;//设置通知点击或滑动时不被清除
        NotificationManager manager=(NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(notifyId,notification);
        updateNotification();
        /////---------------
    }
    private void updateNotification() {
        Log.d("通知栏播放页updata", "updateNotification: 更新状态栏歌曲新息");
        String title = songlist.get(MusicService.mPosition).getName()+" - "+songlist.get(MusicService.mPosition).getSinger();//更新歌曲标题
        remoteView.setTextViewText(R.id.notification_title, title);
        loadingCover(songlist.get(MusicService.mPosition).getPath());
        //通知栏跳转到播放页面
        Intent intent=new Intent();
        intent.setClass(this, PlayDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("position",MusicService.mPosition);
        Log.d("通知栏传给播放页", "initNotificationBar: 通知栏传Intent mPosition"+mPosition);
        intent.putExtras(bundle);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        notification.contentView = remoteView;
        notification=mBuilder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(notifyId,notification);
    }
    //获取本地歌曲专辑图片
    private void loadingCover(String mediaUri) {
        Bitmap bitmap;
        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaUri);
        Log.d("检查", "loadingCover: ");
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        if(picture!=null) {//如果该歌曲有专辑图则显示
            bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            remoteView.setImageViewBitmap(R.id.notification_album,bitmap);
//            album_pic.setImageBitmap(bitmap);
        }else{//没有专辑图则显示默认图片
//            album_pic.setImageResource(R.drawable.playing);
            remoteView.setImageViewResource(R.id.notification_album,R.drawable.playing);
        }
    }
    void prepare(){
        song = songlist.get(position);
        path = song.getPath();
        Log.d(TAG, "song path:"+path);
        player = new MediaPlayer();//This is only done once, used to prepare the player.
        if (mlastPlayer !=null){
            mlastPlayer.stop();
            mlastPlayer.release();
        }
        mlastPlayer = player;
        mPosition = position;
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(path); //Prepare resources
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                position+=1;
                position=(position+songlist.size())%songlist.size();
                song=songlist.get(position);
                //////////////////////
                mPosition=position;
                /////////////////////////
//                Toast.makeText(context, "切换下一首:"+song.getName(), Toast.LENGTH_SHORT).show();
                prepare();
            }
        });

    }

    private void postState(Context context, int state,int songid) {
        Intent actionIntent = new Intent(MusicService.MAIN_UPDATE_UI);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN,state);
        actionIntent.putExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, songid);
        updateNotification();
        context.sendBroadcast(actionIntent);
    }

    //对音乐的操作
    public class musicBinder extends Binder{
        public boolean isPlaying(){
            return player.isPlaying();//判断当前歌曲是否正在播放
        }
        public void play(){
            if(player.isPlaying())
                player.pause();
            else{
                player.start();
            }
        }

        //播放下一首歌
        public void next(int type){
            mPosition+=type;
            mPosition=(mPosition+songlist.size())%songlist.size();
            song=songlist.get(mPosition);
            prepare();
        }
        //Returns the length of the music in milliseconds
        public int getDuration(){
            return player.getDuration();
        }
        public int getPosition(){return mPosition;}
        //Return the name of the music
        public String getName(){
            return song.getName();
        }
        public String getPath(){
            return song.getPath();
        }
        public String getSinger(){
            return song.getSinger();
        }
        //Returns the current progress of the music in milliseconds
        public int getCurrenPostion(){
            return player.getCurrentPosition();
        }
        public long getAlbumId(){
            return song.getAlbumId();
        }
        //Set the progress of music playback in milliseconds
        public void seekTo(int mesc){
            player.seekTo(mesc);
        }
    }

    //创建广播，发送和接受当前歌曲信息
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action  = intent.getAction();
            if (ACTION.equals(action)) {
                int widget_action = intent.getIntExtra(KEY_USR_ACTION, -1);

                switch (widget_action) {
                    case ACTION_PRE:
                        next(-1);
                        break;
                    case ACTION_PLAY_PAUSE:
                        play();
                        break;
                    case ACTION_NEXT:
                        next(1);
                        break;
                    default:
                        break;
                }
            }
        }
    };
    public void play() {
        if (player.isPlaying()) {
            player.pause();
            postState(getApplicationContext(), VAL_UPDATE_UI_PAUSE,position);
        } else {
            player.start();
            postState(getApplicationContext(), VAL_UPDATE_UI_PLAY,position);
        }
    }

    //Play the next music
    public void next(int type){
        position +=type;
        position = (position + songlist.size())%songlist.size();
        song = songlist.get(position);
        prepare();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}