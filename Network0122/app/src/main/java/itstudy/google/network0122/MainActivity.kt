package itstudy.google.network0122

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //btnTextDownload가 import가 안될 때는 build.gradle에서 설정을 해도 되고 밑에 방법을 이용해도 된다.
        //직접 뷰를 찾아서 변수로 생성
        //val btnTextDownload = findViewById<Button>(R.id.btnTextDownload)

        btnTextDownload.setOnClickListener {
            startActivity(Intent(
                this, TextDownloadActivity::class.java))
        }

        btnImageDownload.setOnClickListener {
            startActivity(Intent(
                this, ImageDownloadActivity::class.java))
        }

        btnHtmlParsing.setOnClickListener {
            startActivity(Intent(
                this,HtmlActivity::class.java))
        }

        btnXMLParsing.setOnClickListener {
            startActivity(Intent(
                this,KhanDomActivity::class.java))
        }
    }
}