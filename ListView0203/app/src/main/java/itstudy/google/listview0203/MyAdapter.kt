package itstudy.google.listview0203

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

//MyAdapter 안에 주생성자 생성
//생성자를 만들고 나면 오버라이딩하라는 에러가 뜨면 implement를 해준다.
class MyAdapter(var context: Context, var data:List<VO>, var layout:Int):BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //position은 인덱스
        //convertView는 재사용을 위해서 넘어오는 뷰
        //이 뷰가 null일 때만 생성해서 출력해주면 됨.

        //내부 클래스에서 convertView를 사용할 수 있도록 지역변수에 대입
        var returnView = convertView

        //레이아웃을 전개할 수 있는 객체를 생성
        var infalater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //재사용 뷰가 없으면 생성
        if (returnView == null){
            returnView = infalater.inflate(layout, parent, false)
        }

        //findViewById는 자신을 출력 중인 Activity나 View를 이용해서만 호출이 가능
        val imgView = returnView?.findViewById<ImageView>(R.id.img)
        //이미지 출력을 위해 id를 준다.
        imgView?.setImageResource(data[position].icon)

        //텍스트 뷰 출력
        val textView = returnView?.findViewById<TextView>(R.id.text)
        textView?.text = data[position].name

        //버튼을 눌렀을 때 처리
        val btn = returnView?.findViewById<Button>(R.id.btn)
        btn?.setOnClickListener {
            //몇 번째 것을 눌렀는지 출력해보기
            Log.e("Select Item",data[position].name!!)
        }

        return returnView!!
    }

    //항목 뷰에 보여질 데이터를 리턴하는 메소드 - 있는 것중 아무거나 리턴하면 됨.
    override fun getItem(position: Int): Any {
        return data[position].name!!
    }

    //각 항목의 ID를 설정하는 메소드
    //행 인덱스를 그대로 사용
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //항목 뷰의 개수를 설정해주는 함수
    //데이터의 개수로 설정하는 경우가 많다.
    override fun getCount(): Int {
        return data.size
    }

}