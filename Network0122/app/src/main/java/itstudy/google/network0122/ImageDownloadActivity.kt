package itstudy.google.network0122

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_download.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class ImageDownloadActivity : AppCompatActivity() {

    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            //msg의 what이 1이면
            if (msg.what == 1){
                //데이터 가져오기
                val bitmap = msg.obj as Bitmap
                //이미지 출력하기
                img.setImageBitmap(bitmap)
            }else if(msg.what == 2){
                //전달된 데이터 가져오기
                val filename = msg.obj as String
                //파일 경로를 생성
                var path = Environment.getDataDirectory().absolutePath +
                        "/data/itstudy.google.network0122/files/" + filename
                //파일의 내용 출력
                img.setImageBitmap(BitmapFactory.decodeFile(path))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_download)

        //이미지를 바로 출력하는 버튼의 클릭 이벤트 처리
        btnImageDisplay.setOnClickListener {
            object :Thread(){
                override fun run() {
                    //스트림 생성
                    val inputStream = URL(
                        "http://file3.instiz.net/data/file3/2019/07/19/f/8/0/f80fc1ca9c108447b04c34063e92b0fc.jpg")
                        .openStream()
                    //비트맵 가져오기
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()

                    //핸들러에게 전송할 메세지 작성
                    val msg = Message()
                    //데이터는 obj에 넣어주면 된다.
                    msg.obj = bitmap
                    msg.what = 1
                    //핸들러에게 메세지 전송
                    handler.sendMessage(msg)
                }
            }.start()
        }


        //이미지 저장 버튼을 눌었을 때 수행되는 코드
        btnImageSave.setOnClickListener {
            //파일의 경로 생성
            var path = Environment.getDataDirectory().absolutePath +
                    "/data/itstudy.google.network0122/files/50.jpg"
            //파일이 있는지 없는지 여부 조사
            if(File(path).exists()){
                //있는 경우에는 바로 출력하면 된다.
                Toast.makeText(this,"파일이 존재 - 다운로드 받지 않음",
                    Toast.LENGTH_SHORT).show()
                Log.e("파일이 존재", "다운로드 받지 않음")

                //이미지 출력
                img.setImageBitmap(BitmapFactory.decodeFile(path))
            }else{
                //없는 경우에는 다운로드 받아서 출력
                Toast.makeText(this,"파일이 없음 - 다운로드 받아서 출력",
                    Toast.LENGTH_SHORT).show()
                Log.e("파일이 없음", "다운로드 받아서 출력")

                //없으면 다운로드를 받아야 하니까 이미지를 다운 받고 핸들러를 이용해서 출력
                object :Thread(){
                    override fun run() {
                        //스트림 생성
                        val url = URL(
                            "http://file3.instiz.net/data/file3/2019/07/19/9/a/e/9aeb9475eb3e8864ed129ef1c5127d94.jpg")
                        val con = url.openConnection() as HttpURLConnection

                        val inputStream = con.inputStream

                        //다운로드 받은 내용을 저장할 바이트 배열 생성
                        Log.e("파일의 크기", con.contentLength.toString())
                        val raster = ByteArray(con.contentLength)
                        //파일 출력을 위한 스트림 생성
                        val fos = openFileOutput("50.jpg", 0)
                        //읽어오는 부분
                        while (true){
                            val len = inputStream.read(raster)
                            Log.e("len", len.toString())
                            if(len <= 0){
                                break
                            }
                            //기록하는 부분
                            fos.write(raster,0,len)
                        }
                        //정리 작업
                        inputStream.close()
                        fos.close()
                        con.disconnect()

                        //핸들러에게 파일 이름을 전송
                        val msg = Message()
                        msg.obj = "50.jpg"
                        msg.what = 2
                    }
                }.start()
            }
        }

    }
}