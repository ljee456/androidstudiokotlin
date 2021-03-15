package itstudy.google.supportdesign0204

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

//뷰의 종류를 구분하기 위한 상수를 가진 클래스를 생성
abstract class ItemVO{
    abstract val type:Int
    //static 을 만들기 위한 Kotlin 문법
    companion object{
        val TYPE_HEADER = 0
        val TYPE_DATA = 1
    }
}

//헤더에 출력할 데이터를 가진 클래스
class HeaderItem(var date:String): ItemVO() {
    override val type:Int
        get() = ItemVO.TYPE_HEADER
}

//항목에 출력할 데이터를 가진 클래스
internal class DataItem(var id:Int, var title:String, var content:String,
                        var completed:Boolean=false) : ItemVO(){
    override val type:Int
        get() = ItemVO.TYPE_DATA
}

//헤더 뷰 설정
class HeaderViewHolder(view: View?):
    RecyclerView.ViewHolder(view!!){
    val headerView = view?.findViewById<TextView>(
        R.id.itemHeaderView)
}

//항목 뷰 설정
class DataViewHolder(view:View):RecyclerView.ViewHolder(view){
    val completeIconView =
        view?.findViewById<ImageView>(R.id.completedIconView)
    val itemTitleView =
        view?.findViewById<TextView>(R.id.itemTitleView)
    val itemContentView =
        view?.findViewById<TextView>(R.id.itemContentView)
}

class ToDoListActivity : AppCompatActivity() {
    //데이터베이스에서 읽어온 목록을 저장할 리스트
    var list:MutableList<ItemVO> = mutableListOf()

    //RecyclerView에 데이터를 출력하기 위한 Adapter 클래스
    inner class MyAdapter(val list:MutableList<ItemVO>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        //출력할 뷰의 종류를 결정하기 위한 함수
        override fun getItemViewType(position: Int): Int {
            return list.get(position).type
        }

        //출력할 뷰를 리턴하는 함수
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            //viewType을 가지고 출력할 뷰 모양을 설정
            if(viewType == ItemVO.TYPE_HEADER){
                val layoutInflater = LayoutInflater.from(parent?.context)
                return HeaderViewHolder(
                    layoutInflater.inflate(
                        R.layout.item_header, parent, false))
            }else{
                val layoutInflater = LayoutInflater.from(parent?.context)
                return DataViewHolder(
                    layoutInflater.inflate(
                        R.layout.item_main, parent, false))
            }
        }

        //뷰의 개수를 설정하는 함수
        override fun getItemCount(): Int {
            return list.size
        }

        //데이터를 출력하기 위한 메소드
        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder, position: Int) {
            //데이터 가져오기
            val itemVO = list.get(position)
            //viewType에 따라서 다르게 출력
            if(itemVO.type == ItemVO.TYPE_HEADER){
                //텍스트 뷰에 날짜 출력
                val viewHolder = holder as HeaderViewHolder
                val headerItem = itemVO as HeaderItem
                viewHolder.headerView!!.setText(headerItem.date)
            }else{
                val viewHolder = holder as DataViewHolder
                val dateItem = itemVO as DataItem

                viewHolder.itemTitleView.setText(dateItem.title)
                viewHolder.itemContentView.setText(dateItem.content)

                if(dateItem.completed){
                    viewHolder.completeIconView.setImageResource(
                        R.drawable.icon_completed)
                }else{
                    viewHolder.completeIconView.setImageResource(
                        R.drawable.icon)
                }
            }
        }

    }

    //RecyclerView 의 항목을 장식할 클래스
    inner class MyDecoration():RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            //데이터를 찾기 위해서 인덱스를 가져오기
            val index = parent!!.getChildAdapterPosition(view)
            val itemVO = list.get(index)
            if(itemVO.type == ItemVO.TYPE_DATA){
                view!!.setBackgroundColor(0xFFFFFFFF.toInt())
                ViewCompat.setElevation(view, 10.0f)
            }
            outRect!!.set(20, 10, 20, 10)
        }
    }

    //데이터를 읽어서 RecyclerView 에 출력하는 함수
    fun selectDB(){
        list = mutableListOf()
        //데이터베이스 객체 생성
        val helper = DBHelper(this)
        val db = helper.readableDatabase
        //select 구문 실행
        val cursor = db.rawQuery(
            "Select * from tb_todo order by date desc", null)

        //날짜 생성
        var preDate : Calendar? = null
        while(cursor.moveToNext()){
            val dbdate = cursor.getString(3)
            val date = SimpleDateFormat("yyyy-MM-dd").parse(dbdate)
            val currentDate = GregorianCalendar()
            currentDate.time = date

            //이전 데이터의 날짜와 같은 날짜이면 데이터를 저장하지 않음
            if(!currentDate.equals(preDate)){
                val headerItem = HeaderItem(dbdate)
                list.add(headerItem)
                preDate = currentDate
            }
            //완료 여부
            val completed = cursor.getInt(4) != 0
            //데이터 생성
            val dataItem = DataItem(cursor.getInt(0),
                cursor.getString(1), cursor.getString(2),
                completed)
            list.add(dataItem)
        }

        //RecyclerView 출력 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(list)
        recyclerView.addItemDecoration(MyDecoration())

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)

        //데이터베이스의 데이터를 읽어서 RecyclerView에 출력하는 메소드 호출
        selectDB()

        //플로팅 액션 버튼의 클릭 이벤트 처리
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener{
            val intent = Intent(this, AddToActivity::class.java)
            //하위 액티비티를 출력하고 하위 액티비티가 사라지면 onActivityResult를
            //호출하도록 액티비티를 출력
            startActivityForResult(intent, 10)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10 && resultCode == Activity.RESULT_OK){
            selectDB()
        }
    }
}