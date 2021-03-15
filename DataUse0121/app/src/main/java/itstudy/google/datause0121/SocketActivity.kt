package itstudy.google.datause0121

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import kotlinx.android.synthetic.main.activity_socket.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SocketActivity : AppCompatActivity() {
    //전송되어온 문자열을 저장하기 위한 변수
    var mes = ""

    //네트워크에서 전송받은 데이터를 출력하기 위한 핸들러
    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            textView.append(mes)
        }
    }

    //스레드
    //네트워크에서 문자열을 다운로드 받은 후 핸들러를 호출하는 스레드
    //inner라고 쓰는 이유 : 외부 클래스의 멤버를 사용하기 위해서 지정
    //hostname : 서버의 IP 주소
    inner class SocketThread(var hostname:String) : Thread(){
        override fun run() {
            //소켓 생성
            val socket = Socket(hostname, 11000)

            //메세지 보내기
            val pw = PrintWriter(socket.getOutputStream())
            pw.write(editText.text.toString())
            pw.flush()

            //메세지 읽기
            val br = BufferedReader(
                InputStreamReader(
                    socket.getInputStream()))
            mes = br.readLine()

            //핸들러 호출
            handler.sendEmptyMessage(0)

            br.close()
            pw.close()
            socket.close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socket)

        //버튼을 누를 때 스레드를 생성해서 시작
        button.setOnClickListener {
            val thread = SocketThread("192.168.10.28")
            thread.start()
        }
    }
}