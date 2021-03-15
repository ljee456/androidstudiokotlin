package itlee.google.nodeandroid

import java.io.Serializable

//Android에서는 Component 끼리 데이터를 주고 받을 때 Serializable 인터페이스가 implements 된 것만 가능
class Item: Serializable {
    var itemid:Int? = null
    var itemname:String? = null
    var price:Int? = null
    var description:String? = null
    var pictureurl:String? = null
    var updatedate:String? = null

    override fun toString(): String {
        return "Item(itemid=$itemid, itemname=$itemname, price=$price," +
                " description=$description, pictureurl=$pictureurl, updatedate=$updatedate)"
    }
}