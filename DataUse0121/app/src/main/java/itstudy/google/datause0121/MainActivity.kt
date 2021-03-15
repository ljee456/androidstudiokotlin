package itstudy.google.datause0121

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnResource.setOnClickListener {
            val intent = Intent(this, ResourceReadActivity::class.java)
            startActivity(intent)
        }

        btnSqlite.setOnClickListener {
            val intent = Intent(this, SqliteActivity::class.java)
            startActivity(intent)
        }

        btnPreference.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
    }
}