package itstudy.google.supportdesign0204

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class AddToActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_to)

        //오늘 날짜의 날짜를 문자열로 생성
        val date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd")
        val today = format.format(date)
        //today를 날짜 텍스트 뷰에 대입
        val addDateView = findViewById<TextView>(R.id.addDateView)
        addDateView.text = today

        //텍스트뷰를 클릭했을 때 처리
        addDateView.setOnClickListener{
            //오늘 날짜의 년월일 가져오기
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            //날짜 선택 다이얼로그 출력
            var dateDialog = DatePickerDialog(this,
                object:DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        month: Int,
                        dayOfMonth: Int
                    ) {
                        addDateView.text = "${year}-${month+1}-${dayOfMonth}"
                    }
                }, year, month, day).show()
        }
    }

    //ActionBar에 메뉴를 출력해주는 함수
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //입력 뷰들을 전부 찾아오기
        val addTitleEditView = findViewById<EditText>(R.id.addTitleEditView)
        val addContentEditView = findViewById<EditText>(R.id.addContentEditView)
        val addDateView = findViewById<TextView>(R.id.addDateView)

        if(item?.itemId == R.id.menu_add){
            if (addTitleEditView.text.toString().trim().length > 0 &&
                    addContentEditView.text.toString().trim().length > 0){
                //데이터베이스 저장
                val helper = DBHelper(this)
                //쓰기로 열기
                val db = helper.writableDatabase
                //데이터 삽입을 위한 객체를 생성
                val contentValues = ContentValues()
                contentValues.put("title", addTitleEditView.text.toString())
                contentValues.put("content", addContentEditView.text.toString())
                contentValues.put("date", addDateView.text.toString())
                contentValues.put("completed", 0)
                //저장
                db.insert("tb_todo", null, contentValues)
                db.close()
                //저장이 됐으면 목록화면으로 돌아가기
                setResult(Activity.RESULT_OK)
                finish()
            }else{
                Toast.makeText(this, "제목과 내용은 필수입력", Toast.LENGTH_SHORT).show()
                Log.e("저장 실패", "제목과 내용은 필수입력")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}