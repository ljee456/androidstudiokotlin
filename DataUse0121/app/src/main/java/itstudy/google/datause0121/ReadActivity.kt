package itstudy.google.datause0121


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_read.*

class ReadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)

        //앞의 Activity에서 넘겨준 검색어 가져오기
        val keyword = intent.getStringExtra("keyword")

        //데이터베이스 객체 생성
        val helper = DBHelper(this)
        val db = helper.writableDatabase

        //Select 구문 실행
        val cursor = db.rawQuery(
                "select title, content " +
                        "from tb_memo where title like ? " +
                        "order by _id desc",
            arrayOf<String>("%" + keyword + "%"))

        //결과 순회
        var result:String = ""
        //첫번째 컬럼과 두번째 컬럼의 값을 문자열로 가져와서 result에 추가
        while (cursor.moveToNext()){
            result += cursor.getString(0) + "\n"
            result += cursor.getString(1) + "\n"
        }
        //결과 뷰에 출력
        resultView.text = result
        db.close()
    }
}