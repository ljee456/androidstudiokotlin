package com.example.ui0113

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_click.*

class ClickActivity : AppCompatActivity() {
    inner class ClickImpl : View.OnClickListener {
        override fun onClick(v: View) {
            if (v.id == btn1.id){
                //로그 출력
                Log.e("클릭", "버튼1 클릭")
                //스낵바 출력
                Snackbar.make(v, "버튼1 클릭", Snackbar.LENGTH_SHORT).show()
            }else if(v.id == btn2.id){
                //로그 출력
                Log.e("클릭", "버튼2 클릭")
                //스낵바 출력
                Snackbar.make(v, "버튼2 클릭", Snackbar.LENGTH_SHORT).show()
            }else if(v.id == btn3.id){
                //로그 출력
                Log.e("클릭", "버튼3 클릭")
                //스낵바 출력
                Snackbar.make(v, "버튼3 클릭", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun click(v:View){
        //로그 출력
        Log.e("클릭","버튼4 클릭")
        //스낵바 출력
        Snackbar.make(v,"버튼4 클릭", Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_click)

        /*
        btn1.setOnClickListener(ClickImpl())

        btn2.setOnClickListener(object:View.OnClickListener{
            override fun onClick(v: View) {
                //로그 출력
                Log.e("클릭", "버튼2 클릭")
                //스낵바 출력
                Snackbar.make(v,"버튼2 클릭", Snackbar.LENGTH_SHORT).show()

            }
        })

        btn3.setOnClickListener {
            //로그 출력 - 람다를 바로 대입해서 처리
            v:View -> Log.e("클릭", "버튼3 클릭")
            //스낵바 출력
            Snackbar.make(v,"버튼3 클릭", Snackbar.LENGTH_SHORT).show()
        }
        */

        val clickImpl = ClickImpl()
        /*
        btn1.setOnClickListener(clickImpl)
        btn2.setOnClickListener(clickImpl)
        btn3.setOnClickListener(clickImpl)
         */

        val ar = arrayOf<Button>(btn1, btn2, btn3)
        for (btn in ar){
            btn.setOnClickListener(clickImpl)
        }


        //타이머 이벤트 처리
        btnTimer.setOnClickListener{
            /*
            var i = 0
            while (i <= 10){
                i = i + 1
                Thread.sleep(1000)

                Log.e("i", i.toString())
                //버튼의 텍스트를 i로 수정
                //결과:11만 출력 이유: 이 부분은 모아서 한번에 처리한다.
                btnTimer.text = i.toString()
            }
             */

            val timer = object : CountDownTimer(10000,1000){
                var i = 0

                override fun onFinish() {
                    btnTimer.text="타이머 다시 시작"
                }

                override fun onTick(millisUntilFinished: Long) {
                    i = i + 1
                    btnTimer.text = i.toString()
                    Log.e("i", i.toString())
                }
            }
            timer.start()
        }

    }
}