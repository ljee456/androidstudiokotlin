package itstudy.google.customview0208

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

//이벤트이용 - 하나의 점의 좌표를 저장할 DTO 클래스
//x 와 y, isDraw 프로퍼티를 생성
//x 와 y는 좌표이고 isDraw는 선을 그려야하는 좌표인지 구분하기 위한 프로퍼티
class Vertex internal constructor(var x:Float, var y:Float, var isDraw:Boolean)


//View 클래스로부터 상속
//View 클래스의 Default Constructor가 없으므로 생성자를 만들어서 필요한 데이터를 대입해주어야 한다.
class MyCustomView(context: Context): View(context) {
    override fun onDraw(canvas: Canvas?) {
        //내가 원하는 대로 그릴 거라서 없어도 된다.
        //super.onDraw(canvas)

        //Paint 객체를 생성
        val pnt = Paint()
        pnt.setColor(Color.DKGRAY)
        //선 두께
        pnt.strokeWidth = 50F

        //캔버스를 이용해서 그리기 - 원 모양 그리기
        canvas?.drawColor(Color.WHITE)
        canvas?.drawCircle(100F, 100F, 80F, pnt)

        //캔버스를 이용해서 그리기 - 선 그리기
        canvas?.drawLine(10F, 100F, 300F, 100F, pnt)
    }
}