package com.example.ui0113

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPage1.setOnClickListener {
            //VISIBLE: 페이지가 보이도록,INVISIBLE: 페이지가 안보이도록
            page1.visibility = View.VISIBLE
            page2.visibility = View.INVISIBLE
            page3.visibility = View.INVISIBLE
        }
        btnPage2.setOnClickListener {
            page1.visibility = View.INVISIBLE
            page2.visibility = View.VISIBLE
            page3.visibility = View.INVISIBLE
        }
        btnPage3.setOnClickListener {
            page1.visibility = View.INVISIBLE
            page2.visibility = View.INVISIBLE
            page3.visibility = View.VISIBLE
        }
    }
}