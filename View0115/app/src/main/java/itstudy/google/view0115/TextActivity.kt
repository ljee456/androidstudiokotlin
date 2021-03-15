package itstudy.google.view0115

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.text.style.ImageSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import kotlinx.android.synthetic.main.activity_text.*

class TextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        //텍스트뷰에 스크롤 적용
        spanView.movementMethod = ScrollingMovementMethod()

        //출력할 문자열을 생성
        val data = "대한민국\nimg\nSouth Korea"
        //img 라는 글자의 위치를 찾기
        var start = data.indexOf("img")
        //Spannable을 만들어주는 객체 생성
        val builder = SpannableStringBuilder(data)
        if(start > -1){
            //img 뒷자리의 위치를 찾음
            val end = start + "img".length
            //출력할 이미지 가져오기
            val dr = resources.getDrawable(R.drawable.korea, null)
            //dr 위치와 크기 설정
            dr.setBounds(0,0,dr.intrinsicHeight, dr.intrinsicWidth)
            //ImageSpan 생성
            val span = ImageSpan(dr)
            //start 위치부터 end 까지에 span을 추가
            builder.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        //대한민국 만 굵게 만들기
        //시작 위치를 찾는다.
        start = data.indexOf("대한민국")
        //대한민국 이라는 텍스트가 있다면
        //스타트가 -1 보다 크면 찾은 것
        if(start > -1){
            //마지막 위치를 찾는다.
            val end = start + "대한민국".length
            //필요한 Span 생성
            val styleSpan = StyleSpan(Typeface.BOLD)
            val sizeSpan = RelativeSizeSpan(2.0f)
            //builder에 적용( 무엇을 , 어디서부터 어디까지, 어떻게)
            builder.setSpan(styleSpan, start, end+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(sizeSpan, start, end+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        //builder의 내용을 추가
        spanView.text = builder
    }
}