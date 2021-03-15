package itstudy.google.activity0119

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.speech.RecognizerIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //연락처 실행 버튼 클릭
        btn_contacts.setOnClickListener {
            //연락처 앱 실행
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            startActivityForResult(intent, 10)
        }

        //카메라 앱 실행 버튼 클릭
        btn_camera_data.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 30)
        }

        //음성인식 앱 실행 버튼 클릭
        btn_speech.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "음성인식")
            startActivityForResult(intent, 50)
        }

        //맵 버튼 클릭
        btn_map.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:375662952, 126.9779451"))
            startActivity(intent)
        }

        //웹 브라우저 버튼 클릭
        btn_browser.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://naver.com"))
            startActivity(intent)
        }

        //전화 걸기 버튼 클릭
        btn_call.setOnClickListener {
            //권한 확인 - 있으면 작업을 수행하고 없으면 권한을 요청
            if (ContextCompat.checkSelfPermission(this@MainActivity,
                    Manifest.permission.CALL_PHONE) === PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:010-1111-2222"))
                startActivity(intent)
            }else{
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf<String>(android.Manifest.permission.CALL_PHONE), 1000)
            }
        }
    }

    //startActivityForResult로 호출한 후 Activity가 닫히면 호출되는 메소드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //10번 코드를 가진 Activity가 닫힌 경우 호출
        if (requestCode == 10 && resultCode == Activity.RESULT_OK){
            resultView.setText(data?.dataString)
        }
        //30번 코드를 사진 Activity가 닫힌 경우 호출
        if(requestCode == 30 && resultCode == Activity.RESULT_OK){
            //촬영한 이미지를 이미지 뷰에 출력
            val bitmap = data?.extras!!["data"] as Bitmap
            resultImageView.setImageBitmap(bitmap)
        }
    }
}