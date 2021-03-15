package itstudy.google.thread0120

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_flat_file.*

class FlatFileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flat_file)

        //저장
        btnSave.setOnClickListener {
            //파일에 기록할 수 있는 스트림 생성
            val fos = openFileOutput("test.txt", Context.MODE_APPEND)
            //입력한 내용을 기록
            fos.write(edit.text.toString().toByteArray())
            fos.close()

            edit.setText("")
            Toast.makeText(this,"저장 성공",Toast.LENGTH_SHORT).show()
        }

        //읽기
        btnRead.setOnClickListener {
            //파일 입력 스트림 생성
            val fis = openFileInput("test.txt")
            //파일의 내용을 저장할 배열 생성(파일의 길이만큼 만들고)
            val data = ByteArray(fis.available())
            //파일의 내용 읽기
            fis.read(data)
            fis.close()
            //파일의 내용을 문자열로 변환해서 출력
            edit.setText(String(data))
        }
    }
}