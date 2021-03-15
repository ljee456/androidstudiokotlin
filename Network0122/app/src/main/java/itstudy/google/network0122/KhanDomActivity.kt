package itstudy.google.network0122

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import kotlinx.android.synthetic.main.activity_html.*
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class KhanDomActivity : AppCompatActivity() {

    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            var result = msg.obj as String
            resultView.text = result
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_khan_dom)

        object :Thread(){
            override fun run() {
                //스레드가 시작하는지 로그 찍어보기
                Log.e("스레드", "시작")

                //데이터 다운로드
                val url = URL("http://www.khan.co.kr/rss/rssdata/kh_sports.xml")
                val con = url.openConnection() as HttpURLConnection

                con.connectTimeout = 30000
                con.useCaches = false

                val br = BufferedReader(InputStreamReader(con.inputStream))
                val sb = StringBuilder()

                Log.e("위치","다운받기 전")

                while (true){
                    val line = br.readLine()
                    if (line == null){
                        break
                    }
                    sb.append(line)
                }
                Log.e("위치","다운받기 후")
                br.close()
                con.disconnect()

                Log.e("xml", sb.toString())

                if (!TextUtils.isEmpty(sb.toString())){
                    val factory = DocumentBuilderFactory.newInstance()
                    val builder = factory.newDocumentBuilder()

                    //다운로드 받은 데이터는 스트림으로 변환
                    val istream = ByteArrayInputStream(
                        sb.toString().toByteArray(charset("utf-8")))

                    //메모리에 펼치기
                    val doc = builder.parse(istream)
                    //루트 찾기
                    val root = doc.documentElement
                    //자신이 필요한 데이터 찾기 - title 찾기
                    val items = root.getElementsByTagName("title")

                    //출력 내용 만들기
                    var result = ""

                    //items는 이터레이터가 아니여서 반복문을 돌릴 수 없어서 인덱스로 한다.
                    for (idx in 0 until items.length){
                        //데이터 찾아오기
                        val item = items.item(idx)
                        //태그 안의 내용 찾아오기
                        val  text = item.firstChild
                        val title = text.nodeValue
                        //찾은 내용을 result에 추가
                        result = result + title + "\n"
                    }

                    //핸들러에게 메세지 전달
                    val msg = Message()
                    msg.obj = result
                    handler.sendMessage(msg)
                }
            }
        }.start()
    }
}