package itstudy.google.customview0208

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View

class MainActivity : AppCompatActivity() {
    //점들의 정보를 저장할 List
    var arVertex : MutableList<Vertex>? = null


    inner class MyDrawView(context: Context?): View(context){
        var paint : Paint? = null

        init{
            paint = Paint()
            paint!!.color = Color.BLACK
            paint!!.strokeWidth = 10F
        }

        //화면을 그려주는 메소드
        override fun onDraw(canvas: Canvas?) {
            //arVertex를 순회하면서 선을 그리기
            for (i in arVertex!!.indices){
                if (arVertex!!.get(i).isDraw == true) {
                    canvas?.drawLine(
                        arVertex!!.get(i - 1).x,
                        arVertex!!.get(i - 1).y,
                        arVertex!!.get(i).x,
                        arVertex!!.get(i).y, paint!!)
                }
            }
        }
        //터치 이벤트 처리
        override fun onTouchEvent(event: MotionEvent?): Boolean {
            //터치 동작을 확인해서 좌표를 저장하고 onDraw를 호출
            if(event!!.action == MotionEvent.ACTION_DOWN){
                arVertex!!.add(Vertex(event.x, event.y, isDraw = false))
                return true
            }else if (event!!.action == MotionEvent.ACTION_MOVE){
                arVertex!!.add(Vertex(event.x, event.y, isDraw = true))
                //onDraw 호출
                invalidate()    //전체 화면을 무효화하고 다시 그림
            }

            return super.onTouchEvent(event)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        //setContentView(MyCustomView(this))

        setContentView(MyDrawView(this))
        arVertex = mutableListOf<Vertex>()
    }
}