package itstudy.google.supportdesign0204

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager


//ListView를 가진 Fragment
class OneFragment : ListFragment(){
    //뷰를 생성해주는 함수
    override fun onViewCreated(
            view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ListView 에 출력할 데이터 생성
        val datas = arrayOf<String>(
                "Activity", "Fragment", "View", "Layer")
        //Adapter 생성
        val adapter = ArrayAdapter<String>(
                activity!!, android.R.layout.simple_list_item_1,
                datas)
        this.listAdapter = adapter

    }
}

class TwoFragment : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder:AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setTitle("Dialog Fragment")
        builder.setMessage("대화상자 Fragment")
        builder.setPositiveButton("OK", null)
        return builder.create()
    }
}

class ThreeFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //fragment_three.xml 에 디자인 한 내용을
        //container 영역에 출력
        return inflater.inflate(
                R.layout.fragment_three, container,
                false)
    }
}

class MainActivity : AppCompatActivity() {
    //ViewPager를 위한 Adapter 클래스
    internal class MyPagerAdapter(manager:FragmentManager)
        :FragmentPagerAdapter(manager!!,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

        //ViewPager 가 출력할 수 있는 Fragment List
        var fragments:MutableList<Fragment>? = null

        init{
            fragments = mutableListOf<Fragment>(
                    OneFragment(), ThreeFragment()
            )
        }

        override fun getItem(position: Int): Fragment {
            return fragments!!.get(position)
        }

        override fun getCount(): Int {
            return 2
        }

    }

    var manager:FragmentManager? = null
    var oneFragment:OneFragment? = null
    var twoFragment:TwoFragment? = null
    var threeFragment:ThreeFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Fragment 관련 인스턴스 생성
        manager = supportFragmentManager
        oneFragment = OneFragment()
        twoFragment = TwoFragment()
        threeFragment = ThreeFragment()

        //첫번째 버튼을 눌렀을 때 수행할 코드
        val main_btn1 = findViewById<Button>(R.id.main_btn1)
        main_btn1.setOnClickListener {
            if(!oneFragment!!.isVisible){
                val tf : FragmentTransaction = manager!!.beginTransaction()
                tf.addToBackStack(null)
                tf.replace(R.id.main_container, oneFragment!!)
                tf.commit()
            }
        }

        //두번째 버튼을 눌렀을 때 수행할 코드
        val main_btn2 = findViewById<Button>(R.id.main_btn2)
        main_btn2.setOnClickListener {
            if(!twoFragment!!.isVisible){
                twoFragment!!.show(manager!!, null)
            }
        }

        //번째 버튼을 눌렀을 때 수행할 코드
        val main_btn3 = findViewById<Button>(R.id.main_btn3)
        main_btn3.setOnClickListener {
            if(!threeFragment!!.isVisible){
                val tf : FragmentTransaction = manager!!.beginTransaction()
                tf.addToBackStack(null)
                tf.replace(R.id.main_container, threeFragment!!)
                tf.commit()
            }
        }

        val tf : FragmentTransaction = manager!!.beginTransaction()
        tf.addToBackStack(null)
        tf.replace(R.id.main_container, oneFragment!!)
        tf.commit()

        //ViewPager 에 Adapter 설정
        val viewpager = findViewById<ViewPager>(R.id.pager)
        val adapter = MyPagerAdapter(supportFragmentManager)
        viewpager.adapter = adapter
    }
}