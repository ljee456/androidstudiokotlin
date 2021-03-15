package itstudy.google.localnotification0114

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_dialog.*
import java.util.*

class DialogActivity : AppCompatActivity() {
    //배열의 항목 개수만큼 false로 초기화
    var arSelect = booleanArrayOf(false,false,false,false,false,false,false,false,false,false,false,false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        //기본목록 대화상자 출력
        btnListClick.setOnClickListener {
            AlertDialog.Builder(this@DialogActivity)
                    .setTitle("사용할 프로그래밍 언어를 선택")
                    .setItems(R.array.language){
                        dialog, which ->
                        //출력한 배열 가져오기
                        val languages = resources.getStringArray(R.array.language)
                        //선택한 언어 가져오기
                        val pl = languages[which]
                        //토스트로 출력하기
                        Toast.makeText(this@DialogActivity,pl,Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("취소",null)
                    .show()
        }
        //하나만 선택할 수 있는 대화상자 출력
        btnSingleChoice.setOnClickListener {
            AlertDialog.Builder(this@DialogActivity)
                    .setTitle("프로그래밍 언어 1개만 고르세요.")
                    .setSingleChoiceItems(R.array.language,0){
                        dialog, which ->
                        //출력한 배열 가져오기
                        val languages = resources.getStringArray(R.array.language)
                        //선택한 언어 가져오기
                        val pl = languages[which]
                        //토스트로 출력하기
                        Toast.makeText(this@DialogActivity,pl,Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("확인", null)
                    .show()
        }
        //다중 선택이 가능한 대화상자 출력
        btnMultiChoice.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("여러 개를 선택할 수 있습니다.")
                    .setMultiChoiceItems(R.array.language, arSelect){
                        dialog, which, isChecked ->
                        //선택한 인덱스의 값을 isChecked로 수정
                        arSelect[which] = isChecked
                    }
                    .setPositiveButton("확인"){
                        dialog, which ->
                        //resource에 정의한 배열 가져오기
                        val ar = resources.getStringArray(R.array.language)
                        var select =""
                        //배열을 순회할 때 사용할 인덱스
                        var idx = 0
                        //선택 여부 배열을 순회하면서
                        for (i in arSelect){
                            //선택이 되었다면 그 데이터를 select에 추가
                            if(i == true){
                                select = select + ar[idx] + "\t"
                            }
                            idx = idx + 1
                        }
                        Toast.makeText(this@DialogActivity, select,
                        Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("취소", null)
                    .show()

        }
        //작업 상황을 알려주는 작업 진행 대화상자를 출력
        btnProgress.setOnClickListener {
            //작업 진행 대화상자 생성
            //현재는 대화상자보다 ProgressBar 위젯을 이용하는 것을 권장
            //AlrtDialog 계열은 UI가 좋지 않다고 한다.
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("진행 중")
            progressDialog.setMessage("다운로드 중입니다.")
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.setIndeterminate(true)
            progressDialog.show()
        }
        //날짜 선택 대화상자 출력
        //월은 항상 -1을 해준다.
        btnDate.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
                //선택한 년, 월, 일을 토스트로 출력
            Toast.makeText(this@DialogActivity,"${year}.${month+1}.${dayOfMonth}",
            Toast.LENGTH_SHORT).show()

            }, 2021, 0, 14 ).show()
        }
        //시간 선택 대화상자
        btnTime.setOnClickListener {
            //현재 시간
            val cal = Calendar.getInstance()

            TimePickerDialog(this,TimePickerDialog.OnTimeSetListener {
                view, hourOfDay, minute ->
                Toast.makeText(this@DialogActivity,"${hourOfDay}:${minute}분 선택",
                Toast.LENGTH_SHORT).show()
            },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false).show()
        }
        //사용자 정의 대화상자
        btnCustom.setOnClickListener {
            //layout파일에 정의 된 내용을 View로 만들기
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            //뷰 전개
            val view = inflater.inflate(R.layout.custom_layout, null)
            //전개한 뷰를 가지고 대화상자를 생성
            AlertDialog.Builder(this)
                    .setNegativeButton("취소",null)
                    .setView(view)
                    .create()
                    .show()
        }
    }
}