package itstudy.google.supportdesign0204

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class TabUseActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null
    var fragment1:TabOne? = null
    var fragment2:TabTwo? = null
    var fragment3:TabThree? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_use)

        //툴 바 설정
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //액션바에서 타이틀을 출력하지 않도록 설정
        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)

        //프래그먼트 생성
        fragment1 = TabOne()
        fragment2 = TabTwo()
        fragment3 = TabThree()

        //첫번째 프래그먼트를 화면에 출력
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment1!!).commit()

        //탭에 프래그먼트 추가
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.addTab(tabs.newTab().setText("통화기록"))
        tabs.addTab(tabs.newTab().setText("스팸기록"))
        tabs.addTab(tabs.newTab().setText("연락처"))

        //탭을 눌렀을 때 출력할 프래그먼트를 설정하는 이벤트 처리 코드 작성
        tabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            //탭을 다시 선택했을 때 호출되는 메소드
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            //탭의 선택을 해제 했을 때
            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            //탭의 선택했을 때 호출되는 메소드
            override fun onTabSelected(tab: TabLayout.Tab) {
                //선택한 탭의 인덱스를 찾아오기
                val position = tab.position
                //인덱스에 따른 Fragment를 저장할 변수
                var selected : Fragment? = null
                if (position == 0){
                    selected = fragment1
                }else if (position == 1){
                    selected = fragment2
                }else if (position == 2){
                    selected = fragment3
                }

                //출력되는 프래그먼트를 교체
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, selected!!).commit()
            }
        })

        //BottomNavigationView를 클릭했을 때 수행될 내용 작성
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(
            object:BottomNavigationView.OnNavigationItemSelectedListener{
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.itemId){
                        R.id.tab1 -> {
                            Toast.makeText(applicationContext,
                                "첫 번째 탭", Toast.LENGTH_SHORT).show()
                            Log.e("탭","첫 번째 탭")
                            //화면 전환해주기
                            supportFragmentManager.beginTransaction().replace(
                                R.id.container, fragment1!!).commit()
                            return true
                        }
                        R.id.tab2 -> {
                            Toast.makeText(applicationContext,
                                "두두 번째 탭", Toast.LENGTH_SHORT).show()
                            Log.e("탭","두 번째 탭")
                            //화면 전환해주기
                            supportFragmentManager.beginTransaction().replace(
                                R.id.container, fragment2!!).commit()
                            return true
                        }
                        R.id.tab3 -> {
                            Toast.makeText(applicationContext,
                                "세 번째 탭", Toast.LENGTH_SHORT).show()
                            Log.e("탭","세 번째 탭")
                            //화면 전환해주기
                            supportFragmentManager.beginTransaction().replace(
                                R.id.container, fragment3!!).commit()
                            return true
                        }
                    }
                    return false
                }
            })
    }
}