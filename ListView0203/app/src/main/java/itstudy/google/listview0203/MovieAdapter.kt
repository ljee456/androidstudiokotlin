package itstudy.google.listview0203

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import java.net.URL


//생성자에서 context,data,layout을 넘겨받는다
//그리고 BaseAdapter로 부터 상속을 받는다
class MovieAdapter(
    var context: Context,
    var data: MutableList<Movie>,
    var layout:Int):BaseAdapter() {

    //스레드가 전달해준 이미지 뷰와 이미지를 가지고 이미지 뷰에 이미지를 출력해주는 핸들러
    val imageHandler = object: Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message){
            //데이터 가져오기
            val map = msg.obj as MutableMap<String, Any>
            //이미지 뷰 찾아오기
            val imageview = map.get("imageview") as ImageView
            //비트맵 찾아오기
            val bit = map.get("bit") as Bitmap
            //이미지 뷰에 이미지 출력
            imageview.setImageBitmap(bit)
        }
    }

    //이미지를 다운로드 받는 스레드
    inner class ImageThread:Thread(){
        //이미지 파일 이름과 이미지 뷰
        var imagename:String? = null
        var imageview:ImageView? = null

        override fun run() {
            //다운로드 받을 스트림을 생성
            val inputStram = URL("http://cyberadam.cafe24.com/movieimage/${imagename}")
                .openStream()
            //이미지 가져오기
            val bit = BitmapFactory.decodeStream(inputStram)
            //핸들러에게 전달한 메세지를 생성
            val msg = Message()
            val map = mutableMapOf<String, Any>()
            map.put("bit", bit)
            map.put("imageview", imageview!!)
            msg.obj = map
            imageHandler.sendMessage(msg)

        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //재사용 할 뷰가 없으면 생성
        var returnView = convertView
        if (returnView == null){
            val inflater = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            returnView = inflater.inflate(layout, parent, false)
        }

        //데이터를 출력
        val titleView = returnView?.findViewById<TextView>(R.id.movietitle)
        //출력하고 싶은 것을 position에서 꺼내고 출력
        titleView?.text = data[position].title

        val subtitleView = returnView?.findViewById<TextView>(R.id.moviesubtitle)
        subtitleView?.text = data[position].subtitle

        val ratingView = returnView?.findViewById<RatingBar>(R.id.movierating)
        ratingView?.rating = (data[position].rating!! / 5).toFloat()

        //이미지 출력을 위한 코드
        val imageView = returnView?.findViewById<ImageView>(R.id.movieimage)
        val imageThread = ImageThread()
        imageThread.imageview = imageView
        imageThread.imagename = data[position].thumbnail
        imageThread.start()

        return returnView!!
    }

    override fun getItem(position: Int): Any {
        return data[position].title!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        //무조건 데이터의 개수
        return data.size
    }
}