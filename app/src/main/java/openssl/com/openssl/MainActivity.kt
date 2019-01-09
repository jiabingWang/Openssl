package openssl.com.openssl

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class MainActivity : AppCompatActivity() {
    private var mInfoList = ArrayList<AppInfo>() // 第一次获取手机上所有的APP
    private var mFlagList = mutableListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.info_rv)
        val layoutManner = LinearLayoutManager(this) as LinearLayoutManager
        recyclerView.layoutManager = layoutManner
        mInfoList = getAppInfo()
        val arrayAdapter = AppInfoAdapter(sortArrayList(mInfoList),mFlagList,this)
        recyclerView.adapter = arrayAdapter
        Log.d("jiaBing","mFlagList--》"+mFlagList)
    }

    fun getAppInfo(): ArrayList<AppInfo> {
        val mPm = this.packageManager
        val packageInfoList = mPm.getInstalledPackages(0)
        for (infoItem in packageInfoList) {

            if ((infoItem.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0)) {
                //只需要第三方应用
                val appInfo = AppInfo(infoItem.applicationInfo.loadIcon(mPm),
                        mPm.getApplicationLabel(infoItem.applicationInfo).toString(),
                        infoItem.packageName,
                        getCertificateSHA1Fingerprint(infoItem.packageName)!!)
                mInfoList.add(appInfo)
            }
        }
        return mInfoList
    }

    /**
     *  返回签名信息SHA1 Base64.encodeToString
     *  packageName包名
     */
    private fun getCertificateSHA1Fingerprint(packageName: String):String? {
        val flags = PackageManager.GET_SIGNATURES
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = this.packageManager.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val signatures = packageInfo!!.signatures
        val cert = signatures[0].toByteArray()
        val input = ByteArrayInputStream(cert)
        var cf: CertificateFactory? = null
        try {
            cf = CertificateFactory.getInstance("X509")
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        var c: X509Certificate? = null
        try {
            c = cf!!.generateCertificate(input) as X509Certificate
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        var hexString: String? = null
        try {
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(c!!.encoded)
            hexString = Base64.encodeToString(publicKey, Base64.NO_WRAP)
        } catch (e1: NoSuchAlgorithmException) {
            e1.printStackTrace()
        } catch (e: CertificateEncodingException) {
            e.printStackTrace()
        }
        return hexString
    }

    /**
     * 数组排序
     */
    private fun sortArrayList (arrayList : ArrayList<AppInfo> ) :ArrayList<AppInfo>{
        // key 为签名信息。 valve为用了改签名APP的集合
        val map= hashMapOf<String,MutableList<AppInfo>>()
        // 遍历传进来的集合 （手机上所有第三方APK信息） 存在MAP里
        arrayList.forEach {
            val mutableList = map[it.SH1]
            if (mutableList==null){
                map[it.SH1]= mutableListOf(it)
            }else{
                mutableList.add(it)
            }
        }
        // 定义一个集合，存的是 不同签名对应的APP集合
       val mapList= mutableListOf<MutableList<AppInfo>>()
        //遍历map ,将valve存进集合 （valve是一组集合）
        for ((k,v) in map){
            mapList.add(v)
        }
        //将集合排序， 子集合的数量 （6,5,3，3,2....）
       mapList.sortByDescending {
            it.size
        }
        // 最终返回出去的集合
        val list= ArrayList<AppInfo>()
        // 遍历集合，拿出其子集合放入最后的集合
        mapList.forEach {
            if(it.size>1){
                mFlagList.add(it.size)
            }
            list.addAll(it)
        }
        return list
    }

}

