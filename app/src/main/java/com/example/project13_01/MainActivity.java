package com.example.project13_01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listV;
    Button btnPlay, btnPause, btnStop;
    TextView tVTitle;
    ProgressBar pbPlay;

    ArrayList<String> mp3List;
    String selectedMP3;

    String mp3Path = Environment.getExternalStorageDirectory().getPath() + "/Music";
    MediaPlayer mPlayer;
    int pausePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("간단한 mp3플레이어");

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        tVTitle = findViewById(R.id.tvTitle);
        pbPlay = findViewById(R.id.pbPlay);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        mp3List = new ArrayList<>();

//        mp3Path = "/storage/emulated/0/Music";
        File[] listFiles = new File(mp3Path).listFiles();
        String fileName, extName;

        //--------------------------------------------------------
        //TODO mp3파일만 추출하여 ArrayList에 추가
        //TODO getName, substring, add 등의 메소드 사용

        if (listFiles != null) {
            for (File file : listFiles) {
                fileName = file.getName();
                extName = fileName.substring(fileName.lastIndexOf('.') + 1);
                if (extName.equals("mp3"))
                    mp3List.add(fileName);
            }
        } else {
            Log.d("my_mp3 ERROR LOG : ", "파일리스트가 없는데숑");
            try {
                Log.d("my_mp3 ERROR LOG : ", String.valueOf(new File(mp3Path).listFiles().length));
            } catch (Exception e) {
                Log.d("my_mp3 ERROR LOG : ", e.toString());
            }
        }
        //--------------------------------------------------------
        listV = findViewById(R.id.listV);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, mp3List);
        listV.setAdapter(adapter);
        listV.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedMP3 = mp3List.get(position);
                Log.d("my_mp3 ERROR LOG : ", mp3Path + "/" + selectedMP3);
            }
        });

        if (!mp3List.isEmpty()) {
            selectedMP3 = mp3List.get(0);
        }


        btnPlay.setOnClickListener((v) -> {
            try {
                //--------------------------------------------------------
                //TODO 외부저장소의 오디오를 획득하여 재생하고
                //TODO 듣기/중지 버튼 제어
                //TODO TEXTView에 실행중인 음악 파일명 및 ProgressBar 출력
                if(mPlayer==null){
                    btnPlay.setClickable(false);
                    btnPause.setClickable(true);
                    btnStop.setClickable(true);

//                mPlayer = MediaPlayer.create(this, getContentResolver().openInputStream(Uri.parse()));
                    mPlayer = MediaPlayer.create(this, Uri.parse(mp3Path + "/" + selectedMP3));
//                mPlayer.setDataSource(mp3Path + "/" + selectedMP3);
//                mPlayer.prepare();
                    if(mPlayer != null) {
                        mPlayer.start();
                        tVTitle.setText("실행 중인 음악: " + selectedMP3);
                        pbPlay.setVisibility(View.VISIBLE);
                        Log.d("my_mp3 ERROR LOG : ",mPlayer.toString() +"플레이중 ...");
                    } else {
                        Log.d("my_mp3 ERROR LOG : ","로딩 실패...");
                    }
                    //--------------------------------------------------------
                }else if(!mPlayer.isPlaying()){
                    mPlayer.seekTo(pausePosition);
                    mPlayer.start();
                    Log.d("my_mp3 ERROR LOG : ","재시작...");
                }
            } catch (Exception e) {
                Log.d("my_mp3 ERROR LOG : ", e.toString());
            }
        });

        btnPause.setOnClickListener((v)->{
            if (mPlayer!=null && mPlayer.isPlaying()){
                btnPause.setText("이어듣기");
                mPlayer.pause();
                pausePosition = mPlayer.getCurrentPosition();
                pbPlay.setVisibility(View.INVISIBLE);
                tVTitle.setText("일시 중지");
                Log.d("my_mp3 ERROR LOG : ","일시중지...");
            }else if(mPlayer!=null){
                btnPause.setText("일시정지");
                mPlayer.seekTo(pausePosition);
                mPlayer.start();
                pbPlay.setVisibility(View.VISIBLE);
                tVTitle.setText("실행 중인 음악: " + selectedMP3);
                Log.d("my_mp3 ERROR LOG : ","이어듣기...");
            }

        });

        btnStop.setOnClickListener((v) -> {
            btnPlay.setClickable(true);
            btnPause.setClickable(false);
            btnStop.setClickable(false);
            if(mPlayer != null){
                if (mPlayer.isPlaying())
                    Log.d("my_mp3 ERROR LOG : ","stop...");
                mPlayer.stop();
                mPlayer.reset();
                mPlayer=null;
                tVTitle.setText("실행 중인 음악: ");
                pbPlay.setVisibility(View.INVISIBLE);

                btnPause.setText("일시정지");
            }
        });


    }
}