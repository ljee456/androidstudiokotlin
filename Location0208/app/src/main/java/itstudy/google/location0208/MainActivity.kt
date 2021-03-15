package itstudy.google.location0208

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.pedro.library.AutoPermissions.Companion.loadAllPermissions
import com.pedro.library.AutoPermissions.Companion.parsePermissions
import com.pedro.library.AutoPermissionsListener


class MainActivity : AppCompatActivity(), AutoPermissionsListener {

    var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //텍스트 뷰 찾아오기
        textView = findViewById(R.id.textView)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            startLocationService()
        }

        //동적 권한 요청을 위한 메소드 호출
        loadAllPermissions(this, 101)
    }

    //Activity의 동적 권한을 요청하는 메소드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //AutoPermissions 라이브러리에 있는 권한 요청 메소드 호출
        parsePermissions(this, requestCode, permissions as Array<String>, this)
    }

    //거부한 권한을 알려주는 메소드
    //permissions 의 size가 0이 아니면 허용을 하지 않은 권한이 있음
    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        Toast.makeText(this,"거부한 권한:" + permissions.size,
            Toast.LENGTH_SHORT).show()
    }

    //허용한 권한을 알려주는 메소드
    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        Toast.makeText(this,"허용한 권한:" + permissions.size,
            Toast.LENGTH_SHORT).show()
    }

    //위치 정보가 갱신되었을 때 호출될 리스너 클래스
    inner class GPSListener : LocationListener{
        //위치 정보가 변경되었을 때 호출되는 메소드
        override fun onLocationChanged(location: Location) {
            //제일 많이 쓰이는 위도 와 경도 가져오기
            val latitude = location.latitude
            val longitude = location.longitude
            //고도 가져오기 - 에뮬레이터에선 고도가 나오지 않는다.(GPS 센서가 없기 때문에)
            val altitude = location.altitude

            textView!!.text = "내 위치 : 위도-${latitude} 경도-${longitude} 고도-${altitude}"
        }
    }

    //버튼을 누르면 호출될 메소드 - 현재 위치를 텍스트 뷰에 출력하기
    @SuppressLint("MissingPermission") //권한은 나중에 요청하겠다라는 설정
    fun startLocationService(){
        //위치 관리자 가져오기
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //현재 위치 가져오기
        val location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null){
            val latitude = location.latitude
            val longitude = location.longitude
            val altitude = location.altitude

            textView!!.text = "내 위치 : 위도-${latitude} 경도-${longitude} 고도-${altitude}"
        }
        //위치 정보 옵션 설정
        val gpsListener = GPSListener()
        val minTime:Long = 10000
        val minDistance = 0f
        //위치 정보가 갱신될 때 호출할 리스너 설정
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            minTime, minDistance, gpsListener)

        Toast.makeText(this, "위치정보 갱신 리스너 등록", Toast.LENGTH_SHORT).show()
    }

}