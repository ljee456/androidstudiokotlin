package itlee.google.seoultoliet

import android.content.Context
import android.support.v4.os.IResultReceiver
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer


class MyItem(
        val _position:LatLng,
        val _title:String,
        val _snippet:String,
        val _icon:BitmapDescriptor): ClusterItem {
    override fun getSnippet(): String {
        return _snippet
    }

    override fun getTitle(): String {
        return _title
    }

    override fun getPosition(): LatLng {
        return _position
    }

    fun getIcon(): BitmapDescriptor {
        return _icon
    }

    //2개의 데이터 항목이 같은지 비교해주는 메소드
    override fun equals(other: Any?): Boolean {

        if (other is MyItem) {
            return (this.position.latitude == other.position.latitude
                    && this.position.longitude == other.position.longitude
                    && this.title == other.title)
        }
        return false
    }

}

    //마커의 모양을 위한 클래스
    class ClusterRenderer(
        context: Context?,
        map: GoogleMap?,
        clusterManager: ClusterManager<MyItem>
    ) :
        DefaultClusterRenderer<MyItem>(context, map, clusterManager) {
        init {
            clusterManager?.renderer = this
        }

        override fun onBeforeClusterItemRendered(item: MyItem?, markerOptions: MarkerOptions?) {
            //마커 수정
            markerOptions?.icon(item?.getIcon())
            markerOptions?.visible(true)
        }
    }



