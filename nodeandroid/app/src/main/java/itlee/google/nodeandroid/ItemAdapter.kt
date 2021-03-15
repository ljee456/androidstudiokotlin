package itlee.google.nodeandroid

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
import android.widget.TextView
import java.net.URL

//중요 - Kotlin의 특징:Kotlin에서는 상속 관계 전 또는 클래스 이름 뒤에 주생성자를 이용해서 데이터를 대입받을 수 있다.
//Context는 데이터를 저장하고 있는 객체를 의미하는데 Android에서는 Activity를 의미한다.
class ItemAdapter(
//뷰를 출력할 때 필요한 Context(문맥-어떤 작업을 하기 위해 필요한 정보를 저장한 객체) 변수
        var context: Context,
//ListView에 출력할 데이터
        var data: MutableList<Item>,
//항목 뷰에 해당하는 레이아웃의 아이디를 저장할 변수
        var layout: Int
) : BaseAdapter() {

    //xml로 만들어진 레이아웃을 뷰로 변환하기 위한 클래스의 변수
    var inflater: LayoutInflater

    //중요 - init() 이렇게 있으면 보조 생성자의 개념이고 init{}은 초기화 블럭으로 가장 먼저 수행한다.
    init {
        inflater = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
    }

    //출력할 데이터의(행) 개수를 설정하는 메소드
    override fun getCount(): Int {
        return data.size
    }

    //항목 뷰에 보여질 문자열을 설정하는 메소드
    //position은 반복문이 수행될 때의 인덱스
    override fun getItem(position: Int): Any {
        return data[position].itemname!!
    }

    //각 항목뷰의 아이디를 설정하는 메소드
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //ListView에 출력될 실제 뷰의 모양을 설정하는 메소드
    //convertView는 화면에 보여질 뷰인데 처음에는 null이 넘어오고 두번째 부터는
    //이전에 출력된 뷰가 넘어옵니다.
    //인덱스마다 다른 뷰를 출력하고자 하면 convertView를 새로 만들지만
    //모든 항목뷰의 모양이 같다면 처음 한번만 만들면 됩니다.
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        //중요 - Kotlin이나 Swift는 함수의 매개변수가 기본적으로 readonly
        //다른 내용을 대입하고자 할 때는 직접 할 수 없고 참조를 복사해서 수행
        var returnView = convertView
        //convertView 생성
        if (returnView == null) {
            //layout에 정의된 뷰를 parent에 넣을 수 있도록 View로 생성
            returnView = inflater.inflate(layout, parent, false)
        }

        //이미지 출력 - 다운로드 받아서 출력
        val imgView =
                returnView?.findViewById<ImageView>(R.id.itemimage)
        var imagethread = ImageThread()
        imagethread.imagename = data[position].pictureurl
        imagethread.imageview = imgView
        imagethread.start()
        //텍스트 출력
        val itemname = returnView?.findViewById<TextView>(R.id.itemname)
        itemname?.text = data[position].itemname
        val price = returnView?.findViewById<TextView>(R.id.price)
        price?.text = "${data[position].price}원"

        //셀의 높이 조절
        val layoutParams = returnView!!.layoutParams
        layoutParams.height = 200

        returnView!!.layoutParams = layoutParams

        //리턴
        return returnView
    }

    //이미지를 다운로드 받는 스레드
    //중요 - 안드로이드에서 네트워크는 반드시 스레드를 이용해야 한다.
    inner class ImageThread : Thread(){
        var imagename:String? = null
        var imageview:ImageView? = null

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
    //중요 - 안드로이드는 일반 스레드에서 UI 갱신이 안됨
    //그래서 메인 스레드에게 메세지를 보내서 UI 갱신을 하도록 해야 되는데 이 때 사용되는 객체가 Handler이다.
    val imageHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg : Message){
            val map = msg.obj as MutableMap<String, Any>
            val imageview = map.get("imageview") as ImageView
            val bit = map.get("bit") as Bitmap
            imageview.setImageBitmap(bit)
        }
    }
}
