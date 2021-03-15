package itstudy.google.view0115

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //버튼의 클릭 이벤트 처리
        btnScale.setOnClickListener {
            //Animation 가져오기
            val anim : Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.scale)
            //버튼에 애니메이션 적용
            btnScale.startAnimation(anim)
        }

        //버튼의 클릭 이벤트 처리
        btnSet.setOnClickListener {
            //애니메이션 집합 객체를 생성
            val ani = AnimationSet(true)
            ani.interpolator = LinearInterpolator()

            //위치 변환 애니메이션을 생성
            val trans = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.5f)

            trans.duration = 3000
            //애니메이션 집합에 추가
            ani.addAnimation(trans)

            //알파 애니메이션
            val alpha = AlphaAnimation(1.0f, 0.0f)
            alpha.duration = 1000
            alpha.repeatCount = 3
            alpha.repeatMode = Animation.REVERSE
            ani.addAnimation(alpha)
            //애니메이션 시작
            imageView.startAnimation(ani)
        }
    }
}