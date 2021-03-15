package itlee.google.nodeandroid

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.net.URL

class DetailActivity : AppCompatActivity() {

    //이미지를 이미지 뷰에 출력하는 핸들러
    val imageHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg : Message){
            val map = msg.obj as MutableMap<String, Any>
            val imageview = map.get("imageview") as ImageView
            val bit = map.get("bit") as Bitmap
            imageview.setImageBitmap(bit)
        }
    }

    //이미지를 다운로드받기 위한 스레드
    inner class ImageThread : Thread(){
        var imagename:String? = null
        var imageview: ImageView? = null

        override fun run(){
            val inuptStream = URL("http://192.168.10.47/item/img/${imagename}").openStream()
            val bit = BitmapFactory.decodeStream(inuptStream)
            inuptStream.close()
            val message = Message()
            val map = mutableMapOf<String, Any>()
            map.put("bit", bit)
            map.put("imageview", imageview!!)
            message.obj = map
            imageHandler.sendMessage(message)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //자신을 호출할 Intent에서 전달한 데이터를 가져오기
        //자료형 과 name:item 을 잘 맞춰준다.
        val intent = intent
        val item = intent.getSerializableExtra("item") as Item

        val itemname = findViewById<TextView>(R.id.itemname)
        val price = findViewById<TextView>(R.id.price)
        val description = findViewById<TextView>(R.id.description)
        val updatedate = findViewById<TextView>(R.id.updatedate)

        itemname.text = item.itemname
        price.text = "${item.price}원"
        description.text = item.description
        updatedate.text = item.updatedate
        val imageThread = ImageThread()
        imageThread.imagename = item.pictureurl
        imageThread.imageview = findViewById<ImageView>(R.id.picture)
        imageThread.start()

        val backBtn = findViewById<Button>(R.id.back)
        backBtn.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}