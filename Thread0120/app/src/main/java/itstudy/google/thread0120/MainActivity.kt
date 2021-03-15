package itstudy.google.thread0120

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //핸들러 객체 생성
    //object:Handler를 주면 Deprecated가 되서 Looper.getMainLooper()로 없애준다.
    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            //추상 클래스여서 지워도됨.
            //super.handleMessage(msg)
            //데이터 가져오기
            val data = msg.obj as Int
            //출력
            txtResult.text = "데이터:${data}"
        }
    }

    //post 형식으로 메세시지가 전달된 경우 처리하는 핸들러 객체
    val postHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.setOnClickListener{
            //1초마다 1부터 10까지 순서대로 출력
            for (i in 1..10){
                Log.e("i", i.toString())
                txtResult.setText("i${i}")
                Thread.sleep(1000)
            }
        }

        btn2.setOnClickListener {
            /*
            //Thread 클래스를 상속받아서 생성
            object :Thread(){
                override fun run() {

                }
            }.start()
             */

            //Runnable 인터페이스 구현해서 생성
            Thread(Runnable {
                for (i in 1..10){
                    txtResult.text = "i:${i}"
                    Log.e("1", i.toString())
                    Thread.sleep(1000)
                }
            }).start()
        }

        btn3.setOnClickListener {
            Thread(Runnable {
                for (i in 1..10) {
                    //핸들러에게 전송할 내용
                    val msg = Message()
                    //데이터 저장
                    msg.obj = i
                    //핸들러에게 메시지를 전송
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                }
            }).start()
        }

        btn4.setOnClickListener {
            Thread(Runnable {
                for (i in 1..10){
                    //핸들러에게 postMessage를 전송
                    handler.post {
                        txtResult.text="데이터:${i}"
                    }
                    Thread.sleep(1000)
                }
            }).start()
        }

    }
}