package itstudy.google.network0122

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import kotlinx.android.synthetic.main.activity_text_download.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class TextDownloadActivity : AppCompatActivity() {

    //화면 출력을 위한 핸들러
    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            val str = msg.obj as String
            resultView.text = str
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_download)

        //버튼 클릭 이벤트
        btnDownload.setOnClickListener {
            object :Thread(){
                override fun run() {
                    //텍스트를 다운로드 받을 URL 생성
                    val url = URL("https://www.google.com")
                    //연결 객체 생성
                    val con = url.openConnection() as HttpURLConnection
                    //옵션 설정
                    con.connectTimeout = 30000
                    con.useCaches = false

                    //문자열을 읽기 위한 스트림을 생성
                    val br = BufferedReader(InputStreamReader(con.inputStream))

                    //String은 모든 작업을 복사해서 수행하기 때문에 문자열 연산을 하면 메모리 낭비가 발생할 수 있음
                    //복사하지 않고 문자열 작업을 수행하는 클래스가 StringBuilder이다.
                    val sb = StringBuilder()

                    //문자열을 읽어서 sb에 추가
                    while (true){
                        val line = br.readLine()
                        if(line == null){
                            break
                        }
                        sb.append(line)
                    }

                    //정리 작업
                    br.close()
                    con.disconnect()

                    //핸들러에게 데이터를 출력하도록 전달하기
                    val msg = Message()
                    //데이터는 obj에 넣어주면 된다.
                    msg.obj = sb.toString()
                    //핸들러에게 메세지 보내기
                    handler.sendMessage(msg)
                }
            }.start()
        }
    }
}