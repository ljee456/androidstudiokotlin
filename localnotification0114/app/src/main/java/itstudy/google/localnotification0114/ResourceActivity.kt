package itstudy.google.localnotification0114

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_resource.*

class ResourceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)

        //이미지 가져오기
        val bitmap = resources.getDrawable(R.drawable.img,null)
        //이미지 뷰에 그리기 - 서버에서 이미지를 가져올 때 이렇게 둔다.
        imageView.setImageDrawable(bitmap)
        //너비와 높이 설정
        imageView.layoutParams.width = bitmap.intrinsicWidth
        imageView.layoutParams.height = bitmap.intrinsicHeight
    }
}