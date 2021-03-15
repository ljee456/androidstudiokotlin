package itstudy.google.listview0203

import java.io.Serializable

class VO:Serializable {
    var icon = 0
    var name:String?=null

    override fun toString(): String {
        return "VO[icon=${icon}, name=${name}"
    }
}