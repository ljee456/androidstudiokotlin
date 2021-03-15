package itlee.google.seoultoliet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.ClusterRenderer
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity(), AutoPermissionsListener {

    //맵의 기본 줌 레벨
    val DEFULT_ZOOM_LEVEL = 15F
    //위치 정보를 가져오지 못했을 때 사용할 기본 위치 정보
    val CITY_HALL = LatLng(37.5662952, 126.9779450)
    //구글 맵을 위한 프로퍼티
    var googleMap: GoogleMap? = null
    //권한 설정 여부를 저장할 프로퍼티
    var isGranted = false
    //맵 뷰를 참조할 프로퍼티
    var mapView: MapView? = null


    //스낵바 출력을 위해 메인 뷰를 저장할 프로퍼티 선언
    var mainContainer:ConstraintLayout? = null
    //공공 데이터 API 키
    val API_KEY = "6250566d476c6a6532355772486f50"
    //화장실 위도와 경도를 저장할 List
    val toiletList:MutableList<Map<String, Any>> = mutableListOf<Map<String, Any>>()
    //화장실 이미지 생성 - 지연 생성(처음 사용할 때 만들어내는 것)
    val bitmap by lazy {
        val drawable = resources.getDrawable(
            R.drawable.restroom_sign, null) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 64, 64, false)
    }


    //클러스터링을 위한 프로퍼티
    var clusterManager:ClusterManager<MyItem>? = null
    var clusterRenderer: ClusterRenderer<MyItem>? = null




    //현재 위치를 리턴해주는 함수
    @SuppressLint("MissingPermission")
    fun getMyLocation():LatLng{
        //위치 확인을 위한 공급자 생성
        val locationProvider:String = LocationManager.GPS_PROVIDER
        //위치 서비스 객체를 가져온다
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //마지막 업데이트 된 위치 가져오기
        val lastLocation: Location? = locationManager.getLastKnownLocation(locationProvider)

        return LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
    }




    //toiletList의 데이터를 가지고 마커를 출력하기 위한 함수
    fun addMarker(toilet:MutableMap<String, Any>){
        /*
        googleMap?.addMarker(MarkerOptions()
            .position(LatLng(toilet.get("LAT") as Double, toilet.get("LNG") as Double))
            .title(toilet.get("FNAME") as String)
            .snippet(toilet.get("ANAME") as String)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        )
         */

        //ClusterManager를 이용해서 마커 출력
        clusterManager?.addItem(MyItem(LatLng(
            toilet.get("LAT") as Double, toilet.get("LNG") as Double),
            toilet.get("FNAME") as String,
            toilet.get("ANAME") as String,
            BitmapDescriptorFactory.fromBitmap(bitmap)))
    }




    //json 문자열을 다운로드 받는 스레드
    inner class ToiletThread: Thread(){
        override fun run() {
            //시작 인덱스와 종료 인덱스를 저장할 프로퍼티
            var startIdx = 1
            var endIdx = 100

            //데이터 전체 개수를 저장하기 위한 프로퍼티
            //기본값을 0
            var count = 0

            //100번을 반복
            do {
                //URL을 생성
                val addr = "http://openapi.seoul.go.kr:8088/6250566d476c6a6532355772486f50/json/SearchPublicToiletPOIService/${startIdx}/${endIdx}/"
                //URL로 바꿔주기
                val url = URL(addr)
                //URL 연결 객체 생성
                val connection = url.openConnection()

                //데이터를 바이트 단위로 읽어서 문자열로 변환
                //문자열의 길이가 짧을 때 사용
                val data = connection.getInputStream().readBytes()
                    .toString(charset("utf-8"))

                //URL에서 데이터를 확인하고 받아온 문자열을 JSONObject로 변환
                var jsonData = JSONObject(data)
                //SearchPublicToiletPOIService 키로 객체 가져오기
                var root = jsonData.getJSONObject("SearchPublicToiletPOIService")

                //데이터 개수를 찾아서 count에 저장하기
                count = root.getInt("list_total_count")

                //데이터 목록의 배열 찾아오기 - 키 이름은 row
                val row = root.getJSONArray("row")
                for (i in 0 until row.length()){
                    //배열 안의 요소들을 가져오기
                    val obj = row.getJSONObject(i)
                    //필요한 속성들을 찾아서 하나의 객체를 만들고 이 객체를 리스트에 추가
                    val map = mutableMapOf<String, Any>()
                    //FNAME(문자열), ANAME(문자열), Y_WGS84(위도 - 실수), X_WGS84(경도 - 실수)
                    map.put("FNAME", obj.getString("FNAME"))
                    map.put("ANAME", obj.getString("ANAME"))
                    map.put("LAT", obj.getDouble("Y_WGS84"))
                    map.put("LNG", obj.getDouble("X_WGS84"))
                    toiletList.add(map)
                }

                startIdx = startIdx + 100
                endIdx = endIdx + 100
            }
            //startIdx가 count보다 작으면 계속
            while (startIdx < count)
            Log.e("가져온 데이터", toiletList.toString())
            //핸들러를 호출해서 UI 갱신해달라고 요청
            handler.sendEmptyMessage(1)
        }
    }




    //스레드로부터 메세지를 받아서 addMarker를 호출하는 핸들러
    val handler = object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            //화장실 정보 목록을 순회하면서 addMarker를 호출
            for (temp in toiletList){
                addMarker(temp as MutableMap<String, Any>)
            }

            //클러스터링 실행
            clusterManager?.cluster()
        }
    }




    //Activity가 생성될 때 호출되는 메소드
    //주로 하는 일은 필요한 것을 찾아오는 것 또는 맨 처음 1번만 수행하는 작업을 수행
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //스낵바를 출력하기 위해 메인 뷰를 찾아오기
        mainContainer = findViewById(R.id.mainContainer)
        //디자인 한 뷰 찾아오기
        mapView = findViewById(R.id.mapView)
        //맵 뷰 초기화
        mapView!!.onCreate(savedInstanceState)

        var myLocationButton:FloatingActionButton = findViewById(R.id.myLocationButton)


        //앱이 실행되면 지도를 출력하기
        if(isGranted){
            //권한이 있으면 바로 부르고
            initMap()
        }else{
            //권한이 없으면 권한을 요청
            AutoPermissions.loadAllPermissions(this, 101)
        }

        //버튼 눌렀을 때 처리
        myLocationButton.setOnClickListener {
            onMyLocationButtonClick()
        }

        //테스트를 위해 onCreate에서 한 것
        //ToiletThread().start()
    }




    //데이터를 다운로드 받기 위한 스레드 변수
    var toiletThread : ToiletThread? = null
    //앱이 활성화 될 때 호출되는 메소드 : 대부분 onResume에서 처리함
    override fun onStart() {
        super.onStart()
        //스레드가 동작 중이지 않으면 스레드를 시작
        if (toiletThread == null){
            toiletThread = ToiletThread()
            toiletThread!!.start()
        }
    }
    //앱이 비활성화 될 때 호출되는 메소드 : 대부분 onPause에서 처리함
    override fun onStop() {
        super.onStop()
        //스레드가 동작 중이면 스레드를 멈추고 삭제
        toiletThread!!.isInterrupted
        toiletThread = null
    }




    //Activity가 화면에 보여질 때 마다 호출되는 메소드
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    //Activity가 비활성화, 화면에 사라진다던가 동작이 중지 되었을 때 호출되는 메소드
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    //Activity가 파괴될 때 호출되는 메소드
    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    //배터리가 부족할 때 호출되는 메소드
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }




    //맵을 초기화해주는 사용자 정의 함수
    @SuppressLint("MissingPermission")
    fun initMap(){
        //MapView에서 구글 맵을 호출하는 함수 - 비동기적으로 요청한다
        mapView?.getMapAsync {

            //클러스터링을 위한 초기화 작업
            clusterManager = ClusterManager(this, it)
            clusterRenderer = ClusterRenderer(this, it, clusterManager!!)
            it.setOnCameraIdleListener(clusterManager)
            it.setOnMarkerClickListener(clusterManager)

            //구글 맵 객체 저장
            googleMap = it
            //플로팅 액션 버튼 - 현재 이동 버튼을 비활성화시킨다
            it.uiSettings.isMyLocationButtonEnabled = false
            //위치 사용 권한에 따른 분기
            when{
                //isGranted가 true이면, 거부한 권한이 없다면
                isGranted -> {
                    //플로팅 액션 버튼을 true로 변경해서 현재 위치 활성화
                    it.isMyLocationEnabled = true
                    //시점을 이동(지도 이동)
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        getMyLocation(), DEFULT_ZOOM_LEVEL))
                }
                //isGranted가 false이면 , 거부한 권한이 있다면
                else -> {
                    //서울 시청으로 이동시킨다
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        CITY_HALL, DEFULT_ZOOM_LEVEL))
                }
            }
        }
    }




    //플로팅 액션 버튼을 누를 때 호출될 함수
    //권한 여부에 따라 지도의 포커스를 현재 위치로 옮기는 역할
    fun onMyLocationButtonClick(){
        when{
            isGranted -> googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(
                getMyLocation(), DEFULT_ZOOM_LEVEL))

            else -> Snackbar.make(mainContainer!!, "위치 사용 권한이 없습니다",
                Snackbar.LENGTH_SHORT).show()
        }
    }




    //권한을 요청하고 그 결과가 전달되었을 때 호출되는 메소드 : Activity 클래스의 메소드이다
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //권한 허락의 결과를 AutoPermissions에게 전달
        AutoPermissions.parsePermissions(
                this, requestCode, permissions as Array<String>, this)
    }

    //Denied : 거부한 권한들의 집합을 알려주는 메소드
    //Log 나 Toast, SnackBar를 출력할 것인지 정한다
    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        //스낵바를 이용해서 거부한 권한의 개수를 출력
        Snackbar.make(mainContainer!!, "거부한 권한은" + permissions.size + "개",
            Snackbar.LENGTH_SHORT).show()

        //거부한 권한이 1개라도 있으면 그 내용을 표시해주기
        //거부한게 없으면
        if (permissions.size == 0){
            isGranted = true
            //맵을 초기화 해주는 메소드를 호출
            initMap()
            //거부한게 있으면
        }else{
            isGranted = false
        }
    }

    //Granted : 허가한 권한들의 집합을 알려주는 메소드
    //Log 나 Toast, SnackBar를 출력할 것인지 정한다
    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        //스낵바를 이용해서 허용한 권한의 개수를 출력
        Snackbar.make(mainContainer!!, "허용한 권한은" + permissions.size + "개",
            Snackbar.LENGTH_SHORT).show()
    }
}