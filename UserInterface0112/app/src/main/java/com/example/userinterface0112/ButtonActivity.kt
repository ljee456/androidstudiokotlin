package com.example.userinterface0112

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_button.*

class ButtonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)

        //버튼 누를 때 수행할 코드
        btn.setOnClickListener {
            text.text = "버튼을 누름"
        }
    }
}