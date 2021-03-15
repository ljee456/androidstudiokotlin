package itstudy.google.webview0118

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sub.*

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        //앞의 Activity가 전달한 데이터를 읽기
        val intent = getIntent()
        val msg = intent.getStringExtra("name")
        lblSub.text = msg

        //버튼의 클릭 이벤트
        btnMainCall.setOnClickListener {
            //인텐트 생성
            val intent = Intent()
            //데이터 저장
            intent.putExtra("cafe","스타벅스")
            //응답 코드와 데이터를 전달
            setResult(101,intent)
            //자신의 Activity를 종료
            finish()
        }

    }
}