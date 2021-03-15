package itstudy.google.datause0121

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_preferences.*

class PreferencesActivity : AppCompatActivity() {
    //지연 생성을 이용해서 환경 설정 객체를 생성
    val preference by lazy { getSharedPreferences(
            "PreferenceActivity", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        //환경 설정의 내용 읽어오기 - 실행시키면 시작하자마자 불러온다
        nameField.setText(preference.getString("nameField", ""))
        pushCheckBox.isChecked =
                preference.getBoolean("pushCheckBoxField", false)


        //환경 설정에 저장하기
        saveButton.setOnClickListener {
            preference.edit().putString("nameField", nameField.text.toString()).apply()

            //체크박스 저장
            preference.edit().putBoolean("pushCheckBoxField", pushCheckBox.isChecked).apply()
        }

        //환경 설정에서 읽어오기
        //실제 저장을 하게 되면 우리 눈으로는 볼 수 없어서 읽는 작업을 해준다.
        loadButton.setOnClickListener {
            nameField.setText(preference.getString("nameField", ""))
            pushCheckBox.isChecked =
                    preference.getBoolean("pushCheckBoxField", false)
        }
    }
}