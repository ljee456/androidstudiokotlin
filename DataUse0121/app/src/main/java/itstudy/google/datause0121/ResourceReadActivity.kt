package itstudy.google.datause0121

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_resource_read.*

class ResourceReadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_read)

        btnResourceRead.setOnClickListener {
            //텍스트 파일로 읽을 수 있는 InputStream 생성
            var fis = resources.openRawResource(R.raw.creed)
            //파일의 내용을 저장할 바이트 배열 생성
            val data = ByteArray(fis.available())
            //파일의 내용을 data에 저장하기
            fis.read(data)
            //텍스트 뷰에 출력
            resultView.text = String(data)
        }
    }
}