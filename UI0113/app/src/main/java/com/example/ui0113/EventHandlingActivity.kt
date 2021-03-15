package com.example.ui0113

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class EventHandlingActivity : AppCompatActivity() {
    //클래스 안에 클래스를 만들면 inner클래스
    //뷰 클래스
    //View 클래스는 매개변수가 없는 생성자가 없기 때문에 생성자에서 대입받아서 넘겨주어야 한다.
    inner class MyView(context : Context?) : View(context){
        /*
        override fun onTouchEvent(event: MotionEvent?): Boolean {
            //내부에 클래스를 만든 경우 외부 클래스의 객체를 지정하고자 하는 경우에는
            //this@외부클래스이름 형태로 작성하면 된다.
            Toast.makeText(this@EventHandlingActivity
                    ,"뷰에서 이벤트 처리", Toast.LENGTH_SHORT).show()
            return super.onTouchEvent(event)
        }
        */
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_event_handling)

        //사용자 정의 뷰를 생성
        val myView = MyView(this)
        //사용자 정의 뷰를 Activity의 전체화면으로 설정
        setContentView(myView)

        /*
        //1.클래스를 만들어서 하는 방법
        //인터페이스를 구현한 클래스를 생성
        class TouchImpl : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Toast.makeText(this@EventHandlingActivity,
                        "클래스를 만들어서 구현", Toast.LENGTH_LONG).show()
                return true
            }
        }

        myView.setOnTouchListener(TouchImpl())
         */

        /*
        //2.anonymous class를 만들어서 처리 하는 방법
        myView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Toast.makeText(this@EventHandlingActivity,
                "익명 클래스를 만들어서 구현", Toast.LENGTH_SHORT).show()
                return false
            }
        })
         */


        //3. SAM 방법을 이용한 이벤트 처리하는 방법
        //메소드가 1개인 인터페이스인 경우만 사용 가능
        myView.setOnTouchListener({
            v: View?, event: MotionEvent? ->
            Toast.makeText(this@EventHandlingActivity,
            "SAM을 이용한 이벤트 처리", Toast.LENGTH_SHORT).show()
            true
        })
    }

    /*
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //MotionEvent? 이므로 null 허용 타입이다.
        //이 경우 프로퍼티를 호출할 때 ? 또는 !! 등을 이용해서 null처리를 해야 한다.
        val x = event?.rawX
        val y = event?.rawY
        Toast.makeText(this,"${x}:${y}", Toast.LENGTH_SHORT).show()
        return super.onTouchEvent(event)
    }
     */
}