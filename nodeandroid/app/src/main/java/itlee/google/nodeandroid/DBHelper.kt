package itlee.google.nodeandroid

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//Android에서 SQLite에 접속하기 위한 클래슬
//name:itemdb 는 파일명이므로 아무거나 괜찮다.
class DBHelper(context: Context) : SQLiteOpenHelper(
        context, "itemdb", null, 1) {
    //처음 데이터베이스를 사용할 때 호출되는 메소드
    //보통 여기서 테이블을 생성한다.
    //테이블을 생성할 때 컬럼의 자료형이 없으면 text이다.
    override fun onCreate(db: SQLiteDatabase) {
        val itemSQL = "create table item " +
                "(itemid integer primary key," +
                "itemname," +
                "price integer," +
                "description," +
                "pictureurl," +
                "updatedate)"
        db.execSQL(itemSQL)
    }

    //onUpgrade는 앱이 업그레이드 된 경우이다. - 기존 테이블을 제거하고 새로 생성하는 코드가 대부분이다.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //드랍 테이블로 제거하고
        db.execSQL("drop table item")
        //다시 생성함.
        onCreate(db)
    }
}
