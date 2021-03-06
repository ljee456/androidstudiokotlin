package itstudy.google.network0125

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_item_detail.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class ItemDetailActivity : AppCompatActivity() {
    //조회할 ItemId를 저장할 프로퍼티
    val itemid = 1

    //파싱한 결과를 저장하기 위한 Map
    var map : MutableMap<String, Any>? = null

    //다운로드 받은 문자열을 저장하기 위한 프로퍼티
    var json : String? = null

    //출력할 핸들러
    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if (msg.what == 1){
                //전송된 데이터 읽기
                map = msg.obj as MutableMap<String, Any>
                itemname.setText(map!!["itemname"] as String?)
                description.setText(map!!["description"] as String?)
                price.setText("${map!!["price"] as Int?}")

                //이미지를 다운로드 받는 스레드를 시작
                ThreadEx().start()
            }else if (msg.what == 2){
                //핸들러가 전송해준 데이터를 Bitmap으로 변환해서 이미지 뷰에 출력
                val bitmap = msg.obj as Bitmap
                picture.setImageBitmap(bitmap)
            }
        }
    }


    internal inner class ThreadEx : Thread(){
        override fun run() {
            //이미지 파일의 이름(map의 pictureurl)을 가져오기
            val pictureurl = map!!["pictureurl"] as String

            //이미지 파일의 URL 생성 : http://cyberadam.cafe24.com/img/${pictureurl}
            val url = URL("http://cyberadam.cafe24.com/img/${pictureurl}")

            //이미지를 Bitmap 으로 다운로드 받기
            val inputStream = url.openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val msg = Message()
            msg.obj = bitmap
            msg.what = 2

            handler.sendMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        object :Thread(){
            override fun run() {
                //URL 생성
                val url = URL("http://cyberadam.cafe24.com/item/detail?itemid=${itemid}")
                //URLConntection 생성
                val con = url.openConnection() as HttpURLConnection
                //옵션 설정
                con.connectTimeout = 30000
                con.useCaches = false

                //Log.e("con", con.toString())

                //문자열 다운로드 받기
                val sb = StringBuilder()
                val br = BufferedReader(InputStreamReader(con.inputStream))
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

                //로그 찍어보기
                //Log.e("데이터", sb.toString())

                //받아온 문자열을 json에 대입
                json = sb.toString()
                if (TextUtils.isEmpty(json)){
                    Toast.makeText(this@ItemDetailActivity,
                    "데이터를 받아오지 못했습니다. 네트워크를 확인해보세요", Toast.LENGTH_SHORT).show()
                    return
                }else{
                    //문자열을 JSONObject로 변환
                    val jsonObject = JSONObject(json)
                    //결과에서 result의 값을 가져오면 데이터가 전송되었는지 아닌지 확인이 가능
                    //true이면 item 키에 데이터가 있는 것이고 false이면 없는 것이다.
                    val result = jsonObject.getBoolean("result")
                    if (result == false){
                        Toast.makeText(this@ItemDetailActivity,
                        "itemid 나 파라미터가 잘못되었습니다.",Toast.LENGTH_SHORT).show()
                        return
                    }else{
                        //item 키에 해당하는 데이터를 JSONObject로 가져온다.
                        val item = jsonObject.getJSONObject("item")
                        //앞에 만들었던 map에 데이터 넣기
                        map = mutableMapOf<String, Any>()
                        //필요한 데이터 가져오기
                        map?.put("itemname", item.getString("itemname"))
                        map?.put("price", item.getInt("price"))
                        map?.put("description", item.getString("description"))
                        map?.put("pictureurl", item.getString("pictureurl"))

                        //핸들러에 메세지 저장
                        //구분하기 위한 번호 1을 저장
                        val msg = Message()
                        msg.obj = map
                        msg.what = 1
                        //핸들러에게 메세지 전송
                        handler.sendMessage(msg)
                    }
                }
            }
        }.start()
    }
}