package com.liangyi.yueting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liangyi.yueting.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class PlayDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private MusicConnection conn;
    private String TAG = "DetailsActivity";
    private Button btn_pre;
    private Button btn_play;
    private Button btn_next;
    private ImageView btn_back;
    private ImageView album_pic;
    private SeekBar seekBar;
    private TextView song_info,tv_cur_time,tv_total_time;
    private MusicService.musicBinder musicControl;
    private static final int UPDATE_UI = 0;
    private List<Song> songList;

    MusicReceiver mReceiver;

    //hander更新ui
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case UPDATE_UI:
                    updateUI();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playdetails_activity);
        songList=ScanMusicUtils.getMusicData(this);
        Intent intent=new Intent(this,MusicService.class);
        Bundle bundle=getIntent().getExtras();
        intent.putExtras(bundle);
        conn=new MusicConnection();
        startService(intent);
        Log.d("进入播放页", "onCreate: Intentd传来的position"+bundle.getInt("position"));
        bindService(intent,conn,BIND_AUTO_CREATE);

        mReceiver=new MusicReceiver(new Handler());
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(MusicService.MAIN_UPDATE_UI);
        getApplicationContext().registerReceiver(mReceiver,intentFilter);
        bindView();
    }

    //连接Service
    private class MusicConnection implements ServiceConnection {

        //This method will be entered after the service is started.
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "::MyConnection::onServiceConnected");
            //Get MyBinder in service
            musicControl = (MusicService.musicBinder) service;
            //Update button text
            updatePlayState();
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "::MyConnection::onServiceDisconnected");

        }
    }




    //广播
    public class MusicReceiver extends BroadcastReceiver {
        private final Handler handler;
        public MusicReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int play_pause = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_BTN, -1);
                    int songid = intent.getIntExtra(MusicService.KEY_MAIN_ACTIVITY_UI_TEXT, -1);
                    song_info.setText(songList.get(songid).getName()+"\n"+songList.get(songid).getSinger());
                    switch (play_pause) {
                        case MusicService.VAL_UPDATE_UI_PLAY:
                            btn_play.setBackgroundResource(R.drawable.stop);
                            break;
                        case MusicService.VAL_UPDATE_UI_PAUSE:
                            btn_play.setBackgroundResource(R.drawable.play_fill);//转换播放暂停按钮
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    //绑定布局
    private void bindView(){
        btn_pre = findViewById(R.id.btn_pre);
        btn_play = findViewById(R.id.btn_play);
        btn_next = findViewById(R.id.btn_next);
        btn_back = findViewById(R.id.btn_back);
        seekBar =  findViewById(R.id.sb);
        song_info = findViewById(R.id.nowSongInfo);
        tv_cur_time =findViewById(R.id.tv_cur_time);
        tv_total_time = findViewById(R.id.tv_total_time);
        album_pic=findViewById(R.id.album_pic);
        btn_pre.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_back.setOnClickListener(this);


        //进度条拉进度监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Progress bar change
                if (fromUser) {
                    musicControl.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Start touching the progress bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Stop touching the progress bar
            }
        });
    }

    //控制按钮监听

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_play:
                play(view);
                break;
            case R.id.btn_next:
                next(view);
                break;
            case R.id.btn_pre:
                pre(view);
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    //更新进度条
    private void updateProgress() {
        int currenPostion = musicControl.getCurrenPostion();
        seekBar.setProgress(currenPostion);
    }


    //更新按钮状态
    public void updatePlayState() {
        if(MusicService.mlastPlayer!=null &&MusicService.mlastPlayer.isPlaying()){
            btn_play.setBackgroundResource(R.drawable.stop);
        }else{
            btn_play.setBackgroundResource(R.drawable.play_fill);
        }
    }

    public void play(View view) {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PLAY_PAUSE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayState();
    }

    public void next(View view) {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_NEXT);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayState();
    }

    public void pre(View view) {
        Intent intent = new Intent(MusicService.ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_USR_ACTION,MusicService.ACTION_PRE);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updatePlayState();
    }

    public void updateUI(){
        //获取专辑图
        loadingCover(musicControl.getPath());

        //获取歌曲时长
        int cur_time = musicControl.getCurrenPostion(), total_time = musicControl.getDuration();
        seekBar.setMax(total_time);
        //获取当前进度
        seekBar.setProgress(cur_time);

        String str = musicControl.getName()+"\n"+musicControl.getSinger();
        song_info.setText(str);
        tv_cur_time.setText(timeToString(cur_time));
        tv_total_time.setText(timeToString(total_time));

        updateProgress();



        //主线程更新ui
        handler.sendEmptyMessageDelayed(UPDATE_UI, 500);
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
            album_pic.setImageBitmap(bitmap);
        }else{//没有专辑图则显示默认图片
            album_pic.setImageResource(R.drawable.playing);
        }
    }

    //转化分钟秒数
    private String timeToString(int time) {
        time /= 1000;
        return String.format("%02d:%02d",time/60,time%60);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(musicControl!=null){
            handler.sendEmptyMessage(UPDATE_UI);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出程序，解绑服务
//        unbindService(conn);
        getApplicationContext().unregisterReceiver(mReceiver);
        Log.d("退出播放页", "onDestroy: ");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("暂停播放页", "onStop: ");
        //Stop the progress of the update progress bar
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("进入播放页", "onResume: ");
    }
}