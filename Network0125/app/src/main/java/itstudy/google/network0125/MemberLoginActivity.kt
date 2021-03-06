package itstudy.google.network0125

import android.content.Context
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
import kotlinx.android.synthetic.main.activity_member_login.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MemberLoginActivity : AppCompatActivity() {
    //로그인 정보를 저장할 프로퍼티
    var profileUrl:String? = null   //이미지 파일 이름
    var email:String? = null        //로그인 한 이메일
    var nickname:String? = null     //닉네임

    var regdate:String? = null

    //데이터를 출력할 핸들러
    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if(msg.what == 1){
                val str = msg.obj as String
                Toast.makeText(this@MemberLoginActivity, str, Toast.LENGTH_SHORT).show()
                Log.e("로그인 결과", str)

            }else if(msg.what == 2){
                val str = msg.obj as String
                Toast.makeText(this@MemberLoginActivity, str, Toast.LENGTH_SHORT).show()
                Log.e("로그인 결과", str)

            }else if(msg.what == 3){
                val str = msg.obj as String
                Toast.makeText(this@MemberLoginActivity, str, Toast.LENGTH_SHORT).show()
                Log.e("로그인 결과", str)
                //로그인 성공한 경우에는 이미지를 다운로드 받아서 이미지 뷰에 출력
                val imageDownloadThread = ImageDownloadThread()
                imageDownloadThread.start()

            }else if(msg.what == 4){
                val bit = msg.obj as Bitmap
                Toast.makeText(this@MemberLoginActivity, "이미지 가져오기 성공",
                    Toast.LENGTH_SHORT).show()
                Log.e("다운로드 결과", "이미지 가져오기 성공")
                //이미지 뷰에 출력
                profileimage.setImageBitmap(bit)
            }
        }
    }

    //로그인을 하고 결과를 해석하는 스레드
    inner class LoginThread: Thread(){
        override fun run() {
            //url을 생성하고 연결 옵션 설정
            val url = URL("http://cyberadam.cafe24.com/member/login")
            val con = url.openConnection() as HttpURLConnection

            //옵선 셜정
            con.requestMethod = "POST"
            con.connectTimeout = 30000
            con.useCaches = false

            //생성 부분
            //email 과 pw에 해당하는 파라미터 생성 및 전송
            var data = URLEncoder.encode("email", "UTF-8").toString() + "=" +
                    URLEncoder.encode(emailinput.text.toString().trim(), "UTF-8") + "&" +
                    URLEncoder.encode("pw","UTF-8") + "=" +
                    URLEncoder.encode(pwinput.text.toString().trim(), "UTF-8")

            //전송 부분
            //서버와의 출력 스트림을 생성
            val wr = OutputStreamWriter(con.outputStream)
            //데이터 전송
            wr.write(data)
            //버퍼의 내용을 비움 - 실제 전송
            wr.flush()

            //전송한 파라미터의 응답 결과 확인
            var sb = StringBuilder()
            var br = BufferedReader(InputStreamReader(con.inputStream))
            while (true){
                var line = br.readLine()
                if(line == null){
                    break
                }
                sb.append(line)
            }
            br.close()
            con.disconnect()

            //Log.e("응답 결과", sb.toString())

            //what으로 구분하기
            val msg = Message()

            if (TextUtils.isEmpty(sb.toString())) {
                msg.what = 1
                //데이터가 안왔다는 것은 주소가 틀린 것이 아니고 서버쪽의 네트워크가 불안정 한 것이다
                msg.obj = "네트워크가 불안정합니다."
            }else{
                //응답 결과 파싱
                val data = JSONObject(sb.toString())

                //login 결과 가져오기 - true/false니까 Boolean
                val login = data.getBoolean("login")
                if(login == false){
                    msg.what = 2
                    msg.obj = "없는 이메일이거나 잘못된 비밀번호 입니다."
                }else{
                    msg.what = 3
                    msg.obj = "로그인 성공"

                    //로그인 정보 가져오기
                    email = data.getString("email")
                    nickname = data.getString("nickname")
                    profileUrl = data.getString("profile")
                    //정수 13자리 라서 Long
                    regdate = data.getLong("regdate").toString()
                    //가져온 로그인 정보를 파일에 저장
                    //txt파일로 생성
                    val fos = openFileOutput("login.txt", Context.MODE_PRIVATE)
                    //기록할 문자열 생성
                    val str = "${email}:${nickname}:${profileUrl}:${regdate}"
                    //파일에 기록
                    fos.write(str.toByteArray())
                    fos.close()
                }
            }
            //핸들러에게 메세지 전송
            handler.sendMessage(msg)
        }
    }


    inner class ImageDownloadThread:Thread(){
        override fun run() {
            //이미지를 다운로드 받기 위한 스트림을 생성
            val inputStream = URL(
                "http://cyberadam.cafe24.com/profile/${profileUrl}").openStream()
            //Bitmap으로 데이터 가져오기
            val bit : Bitmap = BitmapFactory.decodeStream(inputStream)

            //메세지에 담아서 전송
            val msg = Message()
            msg.what = 4
            msg.obj = bit
            handler.sendMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_login)

        //로그인 버튼을 눌렀을 때 이전에 로그인 한 내용이 남아있는지 확인
        try {
            //이전에 로그인 한적이 있는 것
            val fis:FileInputStream = openFileInput("login.txt")
            Log.e("로그인 내역","있음")
        }catch (e:Exception){
            //이전에 로그인 한적이 없는 것
            Log.e("로그인 내역","없음")
        }

        //로그인 버튼 클릭 이벤트 처리
        btnlogin.setOnClickListener {
            //유효성 검사를 해야 함 - 이전꺼에서 복사

            //로그인 시도하는 스레드를 생성해서 시작
            val th = LoginThread()
            th.start()
        }


        //로그아웃 버튼 클릭 이벤트 처리
        btnlogout.setOnClickListener {
            //로그인했을 때 저장한 정보의 파일을 삭제
            var msg:String = " "
            if (deleteFile("login.txt")){
                msg = "로그아웃 하였습니다."
            }else{
                msg = "로그한 적이 없습니다."
            }

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            Log.e("로그아웃", msg)
        }
    }
}