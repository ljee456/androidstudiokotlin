package itstudy.google.webview0118

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_hybrid.*
import kotlinx.android.synthetic.main.activity_main.*

class HybridActivity : AppCompatActivity() {
    class AndroidJavaScriptInterface(aContext: Context){
        private var mContext:Context? = null
        private val handler:Handler = Handler(Looper.getMainLooper())

        @JavascriptInterface
        fun showToastMessage(aMessage:String){
            handler.post(Runnable {
                Toast.makeText(mContext, aMessage, Toast.LENGTH_SHORT).show()
            })
        }

        init {
            mContext = aContext
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hybrid)

        //웹 뷰 설정
        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true
        webview.addJavascriptInterface(AndroidJavaScriptInterface(this),"MyApp")
        webview.loadUrl("http://192.168.10.28:9000/androidweb")

        sendmsg.setOnClickListener{
            //입력한 문자열 가져오기
            val sendmessage = sendtxt.text.toString()
            //자바스크립트 함수를 실행
            webview.loadUrl("javascript:showDisplayMessage('${sendmessage}')")
        }
    }
}