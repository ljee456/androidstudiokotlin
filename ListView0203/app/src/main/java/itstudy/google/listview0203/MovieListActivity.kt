package itstudy.google.listview0203

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MovieListActivity : AppCompatActivity() {
    //다운로드 받은 문자열을 저장할 변수
    var json:String? = null

    //데이터를 다운받아 파싱을 수행해 줄 스레드 변수
    var th : MovieThread? = null

    //데이터 목록을 저장할 변수
    var movieList : MutableList<Movie>? = null

    //데이터 개수를 저장할 변수
    var count:Int? =null

    //ListView에 출력하기 위한 Adapter
    //var movieAdapter : ArrayAdapter<Movie>? = null
    var movieAdapter : MovieAdapter? = null

    //ListView
    var listView : ListView? = null

    //다운로드 진행 상황을 출력할 프로그래스 바
    var downloadview : ProgressBar? = null


    //ListView를 다시 출력하는 핸들러
    var handler:Handler = object:Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            Log.e("핸들러 호출","핸들러 호출됨")
            Log.e("데이터",movieList.toString())
            //ListView를 다시 출력하고 프로그래스 바를 중지
            movieAdapter!!.notifyDataSetChanged()
            downloadview?.visibility = View.GONE
        }
    }

    //스레드 클래스 만들기 - 데이터를 다운로드 받아서 파싱을 수행해주는 스레드
    inner class MovieThread : Thread(){
        override fun run() {
            try{
                //다운로드 받을 URL 생성
                var url = URL("http://cyberadam.cafe24.com/movie/list")

                //다운로드 옵션 생성
                val con = url.openConnection() as HttpURLConnection

                //옵션 설정
                con.requestMethod = "GET"
                con.useCaches = false
                con.connectTimeout = 30000

                //문자열을 다운로드 받기 위한 스트림을 생성
                val br = BufferedReader(InputStreamReader(con.inputStream))

                //중간 중간 문자열을 저장하기 위한 객체를 생성
                val sb = StringBuilder()

                //문자열 읽어오기
                while (true){
                    val line = br.readLine()
                    if(line == null){
                        break
                    }
                    sb.append(line)
                }

                json = sb.toString()

            }catch (e:Exception){
                Log.e("다운로드 실패", e.message!!)
            }

            //Log.e("json", json.toString())

            //json 파싱 수행
            try {
                if (json != null){
                    val data = JSONObject(json)
                    //데이터 개수 설정
                    count = data.getInt("count")
                    //데이터 목록
                    val list = data.getJSONArray("list")
                    var i = 0
                    while (i < list.length()){
                        //배열의 요소를 하나씩 가져오기
                        val item = list.getJSONObject(i)
                        //가져온 것을 movie로 바꿔줌
                        val movie = Movie()
                        movie.movieid = item.getInt("movieid")
                        movie.title = item.getString("title")
                        movie.subtitle = item.getString("subtitle")
                        movie.genre = item.getString("genre")
                        movie.rating = item.getDouble("rating")
                        movie.thumbnail = item.getString("thumbnail")
                        movie.link = item.getString("link")

                        movieList!!.add(movie)

                        i = i + 1
                    }
                }
            }catch (e:Exception){
                Log.e("파싱 실패", e.message!!)
            }

            Log.e("데이터 개수", count.toString())
            Log.e("영화 목록", movieList.toString())

            //핸들러를 호출해서 ListView를 다시 출력해달라고 요청
            handler.sendEmptyMessage(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        //파싱한 영화 목록을 저장할 List를 생성
        movieList = mutableListOf<Movie>()

        //뷰 찾아오기
        listView = findViewById<ListView>(R.id.listview)
        downloadview = findViewById<ProgressBar>(R.id.downloadview)

        //ListView 와 MovieList 연결하기
        /*
        movieAdapter = ArrayAdapter<Movie>(this,
            android.R.layout.simple_list_item_1, movieList!!)

         */
        movieAdapter = MovieAdapter(this, movieList!!, R.layout.movie_cell)

        listView?.adapter = movieAdapter

        //옵션 설정
        listView?.divider = ColorDrawable(Color.RED)
        listView?.dividerHeight = 3

        //스레드를 생성하고 시작
        if (th != null){
            return
        }
        th = MovieThread()
        th!!.start()
    }
}