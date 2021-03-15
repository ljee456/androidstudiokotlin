package itstudy.google.view0115

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class RotateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotate)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        setContentView(R.layout.activity_rotate)
        super.onConfigurationChanged(newConfig)
        //회전 방향 확인
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.e("방향","가로")
            Toast.makeText(this,"가로로 회전", Toast.LENGTH_SHORT).show()
        }else{
            Log.e("방향","세로")
            Toast.makeText(this,"세로로 회전", Toast.LENGTH_SHORT).show()
        }
    }
}