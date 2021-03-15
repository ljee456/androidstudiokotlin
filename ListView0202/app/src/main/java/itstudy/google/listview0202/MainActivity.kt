package itstudy.google.listview0202

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ListView 출력 코드 작성
        val listView = findViewById<ListView>(R.id.listView)

        //출력할 데이터를 생성
        var ar = arrayOf<String>("나이키", "아디다스", "뉴발란스", "디스커버리", "노스페이스")

        /*
        //어댑터 생성
        val adapter = ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, ar)

         */
        //어댑터 수정
        val adapter = ArrayAdapter.createFromResource(
                this, R.array.serverside, android.R.layout.simple_list_item_1)

        //ListView 와 Adapter 연결
        listView.adapter = adapter

        //기타 속성
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.divider = ColorDrawable(Color.RED)
        listView.dividerHeight = 5
    }
}