package itstudy.google.supportdesign0204

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AudioPlayActivity : AppCompatActivity() {
    //오디오 재생기
    private var mediaPlayer:MediaPlayer? = null
    //재생 위치를 저장할 프로퍼티
    private var playbackPosition:Int = 0

    //오디오 재생기 정리를 하는 메소드
    private fun killMediaPlayer(){
        if (mediaPlayer != null){
            mediaPlayer!!.release()
        }
    }

    //오디오 재생을 위한 메소드
    private fun playAudio(){
        //먼저 MediaPlayer가 실행중이면 종료시키기
        killMediaPlayer()

        /*
        //로컬(raw디렉토리)에 있는 음원을 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.birthday)
        mediaPlayer!!.start()

         */

        //서버에 있는 음원을 재생 - AndroidManifest에서 인터넷 권한 부여 및 http 서버 접속 권한 부여 해줄 것
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource("http://cyberadam.cafe24.com/song/tears.mp3")
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_play)


        //버튼 찾아오기
        val startBtn = findViewById<Button>(R.id.playBtn)
        val pauseBtn = findViewById<Button>(R.id.pauseBtn)
        val restartBtn = findViewById<Button>(R.id.restartBtn)

        //시작버튼 눌렀을 떄 재생
        startBtn.setOnClickListener {
            playAudio()
        }
        //중지버튼
        pauseBtn.setOnClickListener {
            //재생 중이었다면
            if (mediaPlayer != null){
                //재생 중인 위치를 저장
                playbackPosition = mediaPlayer!!.currentPosition
                mediaPlayer!!.pause()
            }
        }
        //재시작 버튼
        restartBtn.setOnClickListener {
            //재생 중인지 확인해서 재생이 멈춘 상태이면 재생
            //재생 중이 아니면
            if (mediaPlayer != null && !mediaPlayer!!.isPlaying){
                mediaPlayer!!.start()
                mediaPlayer!!.seekTo(playbackPosition)
            }
        }
    }

    //Activity가 소멸될 때 호출되는 메소드
    override fun onDestroy() {
        super.onDestroy()
        killMediaPlayer()
    }
}