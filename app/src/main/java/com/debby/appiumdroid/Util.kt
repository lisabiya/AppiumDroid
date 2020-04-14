package com.debby.appiumdroid

import android.util.Log
import androidx.core.util.Pair
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import java.util.*

/**
 * Create by wakfu on 2020/4/14
 *
 */
const val SpName = "GetContactPerson"

object Util

fun Util.setData(page: String, map: ArrayList<Pair<String, String>>) {
    val msg = GsonUtils.toJson(map)
    LogUtil.log(msg)
    SPUtils.getInstance(SpName).put(page, msg)
}

fun Util.postDelay(delayTime: Long, doSome: () -> Unit) {
    Timer().schedule(
        object : TimerTask() {
            override fun run() {
                doSome()
            }
        },
        500
    )
}

object LogUtil

fun LogUtil.log(msg: String) {
    Log.e("GetContactPerson", msg)
//    LogUtils.eTag("", msg)
}
