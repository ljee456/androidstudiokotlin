package itlee.google.nodeandroid

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    //업데이트 시간을 저장할 프로퍼티
    //localUpdatetime이 null이면 데이터를 다운로드 한다. - 이유: 가져온적이 없기 때문
    //내가 가지고 있는 업데이트 시간 과 서버가 가지고 있는 업데이트 시간
    //2개의 시간이 다르면 데이터를 갱신 - 다시 다운로드 받기 위해서 저장
    var localUpdatetime:String? = null
    var serverUpdateTime: String? = null

    //페이지 번호
    var pageno: Int? = 1
    //페이지 당 데이터 개수 - 전체화면이나 셀의 개수를 보고 수정한다.
    //하나의 페이지에 표시될 셀의 개수보다 1개 정도 많게 설정해준다.
    //이유 : 아래에 셀이 더 있는 것이 보여야 모든 데이터를 가져온 것이 아니라는 것을 사용자가 알 수 있기 때문
    var pagecount: Int? = 15

    //데이터 목록을 저장할 리스트
    var itemList: MutableList<Item>? = null
    //데이터 개수를 저장할 변수
    //전체 데이터 개수를 저장하고 있으면 다음 페이지의 데이터를 가져올 때 데이터가 있는지 없는지 알려줄 수 있다.
    var count: Int? = null

    //2개가 있어야 한다. - 화면에 보여지는 것 과 데이터 공급해주는 것
    //ListView에 출력하기 위한 Adapter
    var itemAdapter: ItemAdapter? = null
    //데이터 목록을 출력할 뷰
    var listview : ListView? = null

    //다운로드 중인 뷰를 표시해줄 뷰
    var downloadview: ProgressBar? = null

    //데이터 파싱을 위한 프로퍼티 - 다운로드 받은 문자열을 저장할 프로퍼티
    var json:String? = null

    //가장 하단에서 스크롤 했는지 확인하기 위한 프로퍼티
    var lastitemVisibleFlag = false



    //로컬 데이터베이스에 있는 데이터를 출력하는 핸들러
    //Kotlin은 Anonymous 클래스를 만들 때 앞에 object를 붙여줘야 한다.
    //Looper가 메세지를 받아서 처리하기 위한 일종의 Queue이다.
    //getMainLooper는 메세지 Queue를 쓰겠다라는 의미
    var displayHandler: Handler = object : Handler(Looper.getMainLooper()) {
        //handleMessage : 메세지가 전달 될 때 호출되는 메소드
        override fun handleMessage(msg: Message) {
            //로컬 데이터베이스에 읽기 전용으로 접속
            val helper = DBHelper(this@MainActivity)
            val db = helper.readableDatabase
            //데이터 읽기를 수행하고 결과를 cursor에 저장
            //중요 - Cursor, Iterator : 여러 개의 데이터를 행 단위로 접근하기 위한 포인터
            val cursor= db.rawQuery(
                    "select itemid, itemname, price, description, pictureurl, updatedate from item order by itemid desc", null)

            //다음 데이터가 없을 때 까지 다음으로 포인터를 이동
            while (cursor.moveToNext()){
                val item = Item()
                item.itemid = cursor.getInt(0)
                item.itemname = cursor.getString(1)
                item.price = cursor.getInt(2)
                item.description = cursor.getString(3)
                item.pictureurl = cursor.getString(4)
                item.updatedate = cursor.getString(5)
                itemList!!.add(item)
            }
            db.close()
            Log.e("데이터 출력", itemList.toString())
            //ListView의 데이터를 다시 출력
            itemAdapter!!.notifyDataSetChanged()
            //다운로드를 표시해주는 뷰를 화면에서 제거한 것
            downloadview!!.visibility = View.GONE
        }
    }

    //서버로부터 데이터를 다운로드 받아서 JSON 파싱을 수행한 후 결과를 로컬 데이터베이스에 저장하는 스레드
    inner class ItemDownloadThread : Thread() {
        override fun run() {
            try {
                //다운로드 받을 주소 생성
                var url: URL =
                        URL("http://192.168.10.47/item/itemlist?pageno=${pageno}&count=${pagecount}")
                //연결 객체 생성
                val con =
                        url!!.openConnection() as HttpURLConnection

                //옵션 설정
                con.requestMethod = "GET" //전송 방식 선택
                con.useCaches = false //캐시 사용 여부 설정
                con.connectTimeout = 30000 //접속 시도 시간 설정
                //문자열을 다운로드 받기 위한 스트림을 생성
                val br = BufferedReader(InputStreamReader(con.inputStream))
                val sb: StringBuilder = StringBuilder()
                //문자열을 읽어서 저장
                while (true) {
                    val line = br.readLine() ?: break
                    sb.append(line.trim())
                }
                json = sb.toString()
                //읽은 데이터 확인
                Log.e("읽은 데이터", json!!)
                //사용한 스트림과 연결 해제
                br.close()
                con.disconnect()
            } catch (e: Exception) {
                Log.e("다운로드 실패", e.message!!)
            }
            //JSON 파싱하는 부분
            if(json != null) {
                //전체를 JSON 객체로 변환한 것
                val data = JSONObject(json)
                //데이터 개수 저장하기
                //count 키의 값을 정수로 가져오기
                count = data.getInt("count")

                //로컬 파일을 열어서 기록을 함
                //데이터의 양이 작을 때는 데이터베이스를 이용하지 않고 flat(일반)파일을 이용해서 저장한다.
                val fos = openFileOutput(
                        "count.txt",
                        Context.MODE_PRIVATE
                )
                fos.write("${count}".toByteArray())
                fos.close()

                //데이터 목록 가져오기
                //list 키의 값을 배열로 가져오고
                val ar = data.getJSONArray("list")
                //데이터베이스를 쓰기 모드로 연결
                val helper = DBHelper(this@MainActivity)
                val db = helper.writableDatabase
                for (i in 0 until ar.length()) {
                    val obj = ar.getJSONObject(i)
                    //ContentValues : SQLite의 ORM 형태로 사용하기 위한 객체를 생성한 것
                    val row = ContentValues()
                    row.put("itemid", obj.getInt("itemid"))
                    row.put("itemname", obj.getString("itemname"))
                    row.put("price", obj.getInt("price"))
                    row.put("description", obj.getString("description"))
                    row.put("pictureurl", obj.getString("pictureurl"))
                    row.put("updatedate", obj.getString("updatedate"))
                    //데이터베이스에 삽입
                    db.insert("item", null, row)
                }
                db.close()
                displayHandler.sendEmptyMessage(1)
            }
        }
    }


    //업데이트 한 시간을 가져오는 스레드
    inner class UpdateTimeThread : Thread() {
        var task : Int = 1

        override fun run() {
            //업데이트 된 시간을 가져오기
            try {
                //다운로드 받을 주소 생성
                var url: URL =
                        URL("http://192.168.10.47/item/updatedate")
                //연결 객체 생성
                val con = url!!.openConnection() as HttpURLConnection

                //옵션 설정
                con.requestMethod = "GET" //전송 방식 선택
                con.useCaches = false //캐시 사용 여부 설정
                con.connectTimeout = 30000 //접속 시도 시간 설정
                //문자열을 다운로드 받기 위한 스트림을 생성
                val br = BufferedReader(InputStreamReader(con.inputStream))
                val sb: StringBuilder = StringBuilder()
                //문자열을 읽어서 저장
                while (true) {
                    val line = br.readLine() ?: break
                    sb.append(line.trim())
                }
                json = sb.toString()
                br.close()
                con.disconnect()
            } catch (e: Exception) {
                Log.e("다운로드 실패", e.message!!)
            }

            //json 파싱
            if(json != null){
                val data = JSONObject(json)
                serverUpdateTime = data.getString("result")
            }else{
                serverUpdateTime = ""
            }
            //업데이트 한 시간이 없는 경우에는 업데이트 한 시간을 저장하고 데이터를 가져온다.
            if(task == 1){
                val fos = openFileOutput(
                        "updatetime.txt",
                        Context.MODE_PRIVATE
                )
                fos.write(serverUpdateTime?.toByteArray())
                fos.close()
                Log.e("업데이트 시간 저장", serverUpdateTime.toString())
                Log.e("로그", "서버에서 데이터를 새로 가져옵니다.")
                val helper = DBHelper(this@MainActivity)
                val db = helper.writableDatabase
                val itemSQL = "delete from item"
                db.execSQL(itemSQL)
                db.close()
                pageno = 1
                ItemDownloadThread().start()
            }
            //로컬에 업데이트 시간이 있는 경우
            else if(task == 2){
                //로컬의 업데이트 한 시간과 서버의 업데이트 시간이 같으면 데이터를 가져오지 않음
                if(serverUpdateTime.equals(localUpdatetime)){
                    Log.e("로그", "서버에서 데이터를 가져오지 않습니다.")
                    displayHandler.sendEmptyMessage(1)
                    //로컬의 업데이트 시간과 서버의 업데이트 시간이 다르면 데이터를 가져온다.
                }else{
                    val fos = openFileOutput(
                            "updatetime.txt",
                            Context.MODE_PRIVATE
                    )
                    //업데이트를 했으니까 또 기록을 해줘야 한다.
                    fos.write(serverUpdateTime?.toByteArray())
                    fos.close()
                    Log.e("로그", "서버에서 데이터를 다시 가져옵니다.")
                    val helper = DBHelper(this@MainActivity)
                    val db = helper.writableDatabase
                    //item을 지우고
                    val itemSQL = "delete from item"
                    //다시 생성
                    db.execSQL(itemSQL)
                    db.close()
                    ItemDownloadThread().start()
                }
            }
            else if(task == 3){
                Log.e("서버의 업데이트 시간", "${serverUpdateTime}")
                Log.e("로컬의 업데이트 시간", "${localUpdatetime}")
                //서버의 시간과 로컬의 시간이 다르다면
                if(!serverUpdateTime.equals(localUpdatetime)){
                    //로컬의 시간을 서버의 시간으로 수정하고
                    var fos = openFileOutput(
                        "updatetime.txt",
                        Context.MODE_PRIVATE
                    )
                    fos.write(serverUpdateTime?.toByteArray())
                    fos.close()

                    val helper = DBHelper(this@MainActivity)
                    val db = helper.writableDatabase
                    //기존 데이터를 전부 삭제하고
                    val itemSQL = "delete from item"
                    pageno = 1  //페이지 번호 바꾸는 구문 꼭 해주기, 데이터를 다시 가져왔을 때 첫번째 페이지로 가져온다.
                    db.execSQL(itemSQL)
                    db.close()
                    itemList!!.clear()  //이 구문이 없으면 에러는 없지만 업데이트 된 데이터가 보이지 않는다
                    //데이터를 다시 가져온다
                    ItemDownloadThread().start()
                }else{
                    //downloadview?.visibility = View.GONE
                }
            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //프로퍼티 초기화
        itemList = mutableListOf<Item>()
        listview = findViewById<ListView>(R.id.listview)
        downloadview = findViewById<ProgressBar>(R.id.downloadview)

        //ListView에 출력할 데이터를 연결
        itemAdapter = ItemAdapter(
                this, itemList!!, R.layout.item_cell
        )
        listview?.adapter = itemAdapter

        listview?.setDivider(ColorDrawable(Color.RED))
        listview?.setDividerHeight(3)

        //ListView의 Scroll 이벤트 처리
        listview?.setOnScrollListener(object : AbsListView.OnScrollListener{
            override fun onScroll(view: AbsListView?,
                                  firstVisibleItem: Int,
                                  visibleItemCount: Int,
                                  totalItemCount: Int) {
                lastitemVisibleFlag =
                        totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount
            }
            //ListView의 Scroll 이벤트 처리
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                    if (pageno!! * pagecount!! >= count!!) {
                        Toast.makeText(this@MainActivity, "더 이상 데이터가 없습니다.",
                                Toast.LENGTH_LONG).show()
                        return
                    }
                    pageno = pageno!! + 1
                    downloadview!!.visibility = View.VISIBLE
                    ItemDownloadThread().start()
                }
            }
        })

        //SwipeRefreshLayout 찾아오기
        val swipe_layout = findViewById<SwipeRefreshLayout>(R.id.swipe_layout)
        //setOnRefreshListener : ListView 상단에서 하단으로 드래그를 해서 Refresh Control이 보일 때 이벤트 처리를 하는 것
        swipe_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            //다운로드 뷰를 보여주고 Refresh Control을 숨기기
            downloadview?.visibility = View.VISIBLE
            swipe_layout.setRefreshing(false)

            //서버 업데이트 시간을 가져올 준비를 하고
            val updateTimeThread =  UpdateTimeThread()
            //로컬 업데이트 시간을 가져와서 시간을 읽음
            val fis = openFileInput("updatetime.txt")

            val data = ByteArray(fis.available())
            while (fis.read(data) != -1) {
                Log.e("시작", "로컬 파일 읽기")
            }
            localUpdatetime = String(data)
            Log.e("로컬 업데이트 시간", localUpdatetime.toString())
            fis.close()

            //task를 3번으로 해서 서버의 업데이트 시간을 가져오는 스레드를 실행시킨다
            updateTimeThread.task = 3
            updateTimeThread.start()
        })

        //ListView의 cell을 클릭했을 때 이벤트 처리
        listview?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                //첫번째 매개변수는 이벤트가 발생한 ListView
                //두번째 매개변수는 이벤트가 발생한 항목 뷰
                //세번째 매개변수는 이벤트가 발생한 인덱스
                //네번째 매개변수는 이벤트가 발생한 항목 뷰의 아이디

                //데이터를 가져오기
                val item: Item = itemList!!.get(position)
                //출력할 Activity를 Intent로 생성
                val intent: Intent = Intent(this, DetailActivity::class.java)
                //Intent의 데이터를 저장하고
                intent.putExtra("item", item)
                //화면 출력
                startActivity(intent)
            }
    }


    //Activity가 활성화 될 때 호출되는 메소드
    override fun onResume() {
        super.onResume()
        //서버 업데이트 시간을 가져오기
        val updateTimeThread =  UpdateTimeThread()
        try {
            //로컬 업데이트 시간을 가져오기
            val fis = openFileInput("updatetime.txt")
            val data = ByteArray(fis.available())
            while (fis.read(data) != -1) {
                Log.e("시작", "로컬 파일 읽기")
            }
            localUpdatetime = String(data)
            Log.e("로컬 업데이트 시간", localUpdatetime.toString())
            fis.close()

            //데이터 개수 찾아오기
            val fisCount = openFileInput("count.txt")

            val dataCount = ByteArray(fisCount.available())
            while (fis.read(dataCount) != -1) {
                Log.e("시작", "로컬 파일 읽기")
            }
            count = String(dataCount).toInt()
            fisCount.close()
            //로컬에 마지막 업데이트 시간이 저장된 경우는 task 2번으로 스레드를 수행
            updateTimeThread.task = 2
            updateTimeThread.start()
        } catch (e: Exception) {
            //예외가 있는 경우 로컬에 마지막 업데이트 시간이 없는 것 - 이 때는 task 1번으로 스레드를 수행
            Log.e("시작", "로컬 파일 없음")
            updateTimeThread.task = 1
            updateTimeThread.start()
        }
    }

}