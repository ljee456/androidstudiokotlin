package itstudy.google.supportdesign0204

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context)
    :SQLiteOpenHelper(context, "tododb", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        var memoSQL = "create table tb_todo " +
                "(_id integer primary key autoincrement, " +
                "title text, content text, " +
                "date text, completed text) "
        db!!.execSQL(memoSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table tb_todo")
        onCreate(db)
    }

}