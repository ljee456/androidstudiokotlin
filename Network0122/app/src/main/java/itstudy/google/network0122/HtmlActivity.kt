package itstudy.google.network0122

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_html.*
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class HtmlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //화면 갱신을 위한 핸들러
        val handler = object :Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                //데이터 가져오기
                val result = msg.obj as String
                if(result == null){
                    Toast.makeText(this@HtmlActivity, "데이터가 없음",
                        Toast.LENGTH_SHORT).show()
                    Log.e("에러", "데이터가 없음")
                }else{
                    resultView.text = result
                }

            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html)

        object :Thread(){
            override fun run() {
                //https://kream.co.kr/ 의 html 가져오기
                val url = URL("http://www.bloter.net/")
                val con = url.openConnection() as HttpURLConnection
                con.connectTimeout = 30000
                con.useCaches = false

                val br = BufferedReader(InputStreamReader(con.inputStream))

                val sb = StringBuilder()

                while (true){
                    val line = br.readLine()
                    if(line == null){
                        break
                    }
                    sb.append(line)
                }
                br.close()
                con.disconnect()

                Log.e("html", sb.toString())

                //html  파싱
                //isEmpty가 아닐 때
                if (!TextUtils.isEmpty(sb.toString())){
                    //html을 메모리에 펼치기
                    val doc = Jsoup.parse(sb.toString())
                    //원하는 선택자에 해당하는 데이터만 추출
                    val elements = doc.select("div > div > div > div > h1 > a")

                    //출력할 문자열 변수
                    var result = ""
                    //순회
                    for(element in elements){
                        result = result + element.text() + "\n"
                    }

                    //핸들러에게 데이터 전송
                    val msg = Message()
                    msg.obj = result
                    handler.sendMessage(msg)

                }
            }
        }.start()
    }
}