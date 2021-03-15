package itstudy.google.thread0120

import android.app.ProgressDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_question.*

class QuestionActivity : AppCompatActivity() {
    //작업의 진행 상황을 나타낼 변수
    var value = 0
    //작업 진행 대화상자
    var progressDialog:ProgressDialog? = null

    //진행 상황을 주기적으로 출력하는 핸들러 생성 - ProgressDialog
    val handlerProgressDialog = object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            //value를 하나 증가시키고
            value ++
            Thread.sleep(100)
            //100보다 작으면 대화상자에 값을 적용하고
            if(value < 100){
                progressDialog?.progress = value
                //자신에게 메세지를 다시 전송
                this.sendEmptyMessage(0)
            }else{
                //value가 100보다 크거나 같으면 대화상자 종료
                progressDialog?.dismiss()
            }
        }
    }

    //진행 상황을 주기적으로 출력하는 핸들러 생성 - ProgressView
    val handlerProgressBar = object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            //value를 하나 증가시키고 100보다 작으면 ProgressBar에 값을 적용하고 자신에게 메세지를 다시 전송
            //value가 100보다 크거나 같으면 종료
            value ++
            Thread.sleep(100)
            if(value < 100){
                progressBar.progress = value
                this.sendEmptyMessage(0)
            }
        }
    }

    val handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            for (i in 1..10){
                txtResult.text = "i:${i}"
                Thread.sleep(1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        btnQuestion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("다운로드")
                .setMessage("다운로드 하시겠습니까?")
                //이런식으로 작업하면 작업이 끝날 때 까지 대화상자가 닫히지 않음. - 처리부분이 우선권을 가지기 때문에 갱신하는 부분이 뒤로 밀려버림
                .setPositiveButton("예", DialogInterface.OnClickListener {
                        dialog, whichButton ->
                    for (i in 1..10){
                        txtResult.text = "i:${i}"
                        Thread.sleep(1000)
                    }
                })
                .setNegativeButton("아니오", null)
                .show()
        }



        btnQuestionModify.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("이전 내용 수정")
                .setMessage("이번에는 대화상자가 닫히고 작업 수행")
                .setPositiveButton("예", DialogInterface.OnClickListener {
                        dialog, which ->
                    //야주 짧은 딜레이를 주고 핸들러에게 작업을 수행해 달라고 요청
                    handler.sendEmptyMessageDelayed(0,10)
                })
                .setNegativeButton("아니오", null)
                .show()
        }



        btnProgressDialog.setOnClickListener {
            value = 0
            //대화상자 기본 설정을 해서 출력
            progressDialog = ProgressDialog(this)
            progressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog?.setTitle("DOWNLOAD")
            progressDialog?.setMessage("Waiting")
            progressDialog?.setCancelable(false)
            progressDialog?.show()
            //핸들러에게 메세지를 전송해서 핸들러의 handleMessage 호출
            handlerProgressDialog.sendEmptyMessage(0)
        }



        btnProgressBar.setOnClickListener {
            value = 0
            handlerProgressBar.sendEmptyMessage(0)
        }


    }
}