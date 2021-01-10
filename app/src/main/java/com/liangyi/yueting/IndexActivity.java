package com.liangyi.yueting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.liangyi.yueting.service.MusicService;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndexActivity extends AppCompatActivity {
    private List<Song> songList=new ArrayList<Song>();
    private DrawerLayout drawerLayout;//滑动栏
    private static final int LOAD_SONGLIST=1;
    private static final int CHOOSE_PHOTO = 2;
    private Message message=null;
    private TextView info_buttom;
    private ImageView album_bottom;
    private LinearLayout nowSong;
    private TextView songNumber=null;
    private MusicReceiver mReceiver;
    private NavigationView navigationView;
    SQLiteDatabase db ;
    LinearLayoutManager layoutManager;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case LOAD_SONGLIST:
                    break;
                default:
                    break;
            }
            
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search:

                break;
            case R.id.reload:
                //扫描本地歌曲按钮
                Toast.makeText(IndexActivity.this,"正在搜索本地音乐",Toast.LENGTH_SHORT).show();
                loadSongList();
                Toast.makeText(IndexActivity.this,"搜索到"+songList.size()+"首歌曲",Toast.LENGTH_SHORT).show();
            case R.id.menu:
                break;
            case R.id.onlineMusic:
                Intent intent=new Intent(IndexActivity.this,OnlineMusicActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index_activity);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mReceiver = new MusicReceiver(new Handler());
        IntentFilter itFilter = new IntentFilter();
        itFilter.addAction(MusicService.MAIN_UPDATE_UI);
        registerReceiver(mReceiver, itFilter);

        //初始化侧边栏，并设置点击事件
        navigationView=(NavigationView)findViewById(R.id.nav_view);
        initNavMenu();

        //加载litepal数据库
        /*=================litepal数据库=====================*/
        LitePal.initialize(this);
        db = LitePal.getDatabase();

        //检查登录状态
        checkUserLogin();

        checkPermission();//检查文件读写权限并加载歌曲列表

    }

//    @Override
//    protected void onResumeFragments() {
//        super.onResumeFragments();
//        loadSongList();
//    }
    private void checkUserLogin(){
        //查询有无登录记录
        List<User> findUser= LitePal.where("userStatus=?","1")
                .find(User.class);
        //有用户显示用户名，没用户显示登录
        TextView username=(TextView)navigationView.getHeaderView(0).findViewById(R.id.name);

        //如果无用户登陆过
        if(findUser.size()==0){
            username.setText("请登录");
            //进入登陆页面
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(IndexActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        //当前有登录用户
        if(findUser.size()>0){
            User nowUser=findUser.get(0);
            //显示用户名
            username.setText(nowUser.getUserName());
            //进入个人信息页面
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(IndexActivity.this,UserInfoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("number",nowUser.getUserNumber());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    //滑动菜单Nav_menu点击事件
    private void initNavMenu(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.changeBg:
                        Toast.makeText(IndexActivity.this,"更改背景图",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    //返回背景图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    //判断手机版本号
                    if(Build.VERSION.SDK_INT>=19){
                        //api 19为安卓4.4以上用这个方法处理图片
                        handleImageOnKitKat(data);
                    }
                }
            default:
                break;
        }
    }

    private class MusicReceiver extends BroadcastReceiver {
        private final Handler handler;
        // Handler used to execute code on the UI thread
        public MusicReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            // Post the UI updating code to our Handler
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //加载列表
                    loadSongList();
                }
            });
        }
    }
    public void loadSongList(){
        songList=ScanMusicUtils.getMusicData(IndexActivity.this);//初始化获取歌曲信息
        SongAdapter songAdapter=new SongAdapter(songList);//歌曲适配器
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.mRecyclerView);
        layoutManager=new LinearLayoutManager(IndexActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(songAdapter);//加载歌曲列表

        //设置音乐点击事件
        songAdapter.setOnSongItemClickListen(new SongAdapter.OnSongItemClickListen() {
            @Override
            public void onClick_Song(int position) {
//                Toast.makeText(IndexActivity.this,"position is"+position,Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(IndexActivity.this, PlayDetailActivity.class);
                startActivity(intent);
            }
        });
        if(MusicService.mPosition>0){

        }
        //加载底部控制栏布局
        info_buttom=(TextView)findViewById(R.id.CurrentTitle);
        album_bottom=(ImageView)findViewById(R.id.album_bottom);//底部专辑图
        if(MusicService.mlastPlayer!=null){
            String nowSongInfo=songList.get(MusicService.mPosition).getName()+" - "+songList.get(MusicService.mPosition).getSinger();
            info_buttom.setText(nowSongInfo);//更新歌手歌名
            loadingCover(songList.get(MusicService.mPosition).getPath());//获取专辑图
            nowSong=findViewById(R.id.nowSong);
            nowSong.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    int position = MusicService.mPosition;
                    bundle.putInt("position", position);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(IndexActivity.this, PlayDetailActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            info_buttom.setText("🎧动次打次~");
        }
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
            album_bottom.setImageBitmap(bitmap);
        }else{//没有专辑图则显示默认图片
            album_bottom.setImageResource(R.drawable.playing);
        }
    }
    //检查文件读写权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    loadSongList();
                }else{
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    //检查文件读写权限
    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //没有权限则申请权限
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
        }else{
            loadSongList();
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是doucument类型的Uri，则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
            Log.d("uri类型：", "handleImageOnKitKat: content类型的Uri");
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath=getImagePath(uri,null);
            Log.d("uri类型：", "handleImageOnKitKat: content类型的Uri");
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath=uri.getPath();
            Log.d("uri类型：", "handleImageOnKitKat: file类型的Uri");
        }
        disPlayImage(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path=null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToNext()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void disPlayImage(String imagePath) {
        if(imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            ImageView index_bg=findViewById(R.id.idnex_bg);
            index_bg.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"设置背景图失败",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        //返回该activity加载歌曲列表
        super.onResume();
        //返回activity时，定位到上一次点歌的位置
        layoutManager.scrollToPositionWithOffset(MusicService.mPosition,0);
        checkUserLogin();
    }
}