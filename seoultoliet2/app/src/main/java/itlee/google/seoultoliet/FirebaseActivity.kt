package itlee.google.seoultoliet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*

//데이터 삽입할 클래스 생성
class Post{
    var postId = ""
    var writeId = ""
    var message = ""
    var writeTime:Any = Any()
    var commentCount = 0
}


class FirebaseActivity : AppCompatActivity() {

    //nickname 키의 데이터를 참조하는 파이어베이스 객체
    val ref = FirebaseDatabase.getInstance().getReference("nickname")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)

        //데이터 삽입
        val newRef = FirebaseDatabase.getInstance().getReference("Post").push()
        //삽입할 객체 생성
        val post = Post()
        post.postId = newRef.key.toString() //기본키가 된다.
        post.writeId = "nice"
        post.message = "FireBase Save"
        post.writeTime = ServerValue.TIMESTAMP
        post.commentCount = 0

        //저장
        newRef.setValue(post)
        Log.e("데이터 저장", "성공")

        //ref 삭제
        ref.removeValue()

/*
        //값의 변경이 있는 경우 호출되는 리스너
        ref.addValueEventListener(object :ValueEventListener{
            //데이터 읽기가 취소 된 경우 호출되는 메소드
            override fun onCancelled(error: DatabaseError) {

            }

            //데이터의 값이 변경 된 경우 호출되는 메소드
            override fun onDataChange(snapshot: DataSnapshot) {
                //nickname의 값을 가져오기
                val message = snapshot.value.toString()
                //타이틀로 출력
                supportActionBar?.title = message
                Log.e("변경된 데이터", message)
            }

        })

 */
    }
}