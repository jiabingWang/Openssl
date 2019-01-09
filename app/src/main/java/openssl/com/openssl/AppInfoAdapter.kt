package openssl.com.openssl

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class AppInfoAdapter(var appInfoList: ArrayList<AppInfo>, var flagList: MutableList<Int>, var context: Context) : RecyclerView.Adapter<AppInfoAdapter.VHAppInfo>() {
    private var mFlagList = mutableListOf<Int>()
    init {
        //计算需要下边为红线的项
        getNeedRed(flagList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoAdapter.VHAppInfo {
        return VHAppInfo(LayoutInflater.from(context).inflate(R.layout.item_apk_info, parent,
                false))
    }

    override fun getItemCount(): Int {
        return appInfoList.size
    }

    override fun onBindViewHolder(holder: VHAppInfo, position: Int) {
        holder.imIcon.setImageDrawable(appInfoList[position].icon)
        holder.appNameTv.text = appInfoList[position].appName
        holder.appPackNameTv.text = appInfoList[position].PkName
        holder.appSh1Tv.text = appInfoList[position].SH1
        if(mFlagList.contains(position+1)){
            holder.flagv1.visibility = View.VISIBLE
            holder.flagv2.setBackgroundColor(Color.RED)
        }else{
            holder.flagv1.visibility = View.GONE
            holder.flagv2.setBackgroundColor(Color.BLACK)
        }
    }

    inner class VHAppInfo(view: View) : RecyclerView.ViewHolder(view) {
        var imIcon: ImageView = itemView.findViewById(R.id.app_icon_im) as ImageView
        var appNameTv: TextView = itemView.findViewById(R.id.app_name_tv) as TextView
        var appPackNameTv: TextView = itemView.findViewById(R.id.app_pk_name_tv) as TextView
        var appSh1Tv: TextView = itemView.findViewById(R.id.app_sh1_tv) as TextView
        var flagv1: View = itemView.findViewById(R.id.flag_v1) as View
        var flagv2: View = itemView.findViewById(R.id.flag_v2) as View

    }
    var mCount = 0
    fun getNeedRed(flagList: MutableList<Int>){
        flagList.let {
            for ( node in flagList){
                mCount += node
                mFlagList.add(mCount)
            }
        }
    }
}