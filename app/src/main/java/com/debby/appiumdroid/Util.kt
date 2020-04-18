package com.debby.appiumdroid

import android.util.Log
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import java.util.*

/**
 * Create by wakfu on 2020/4/14
 *
 */
const val SpName = "GetContactPerson"
const val PageName = "PageName"

object Util

fun Util.setData(page: String, map: Any) {
    val msg = GsonUtils.toJson(map, false)
    LogUtil.log(msg)
    SPUtils.getInstance(SpName).put(page, msg)
}

fun Util.setPageCount(page: Int) {
    SPUtils.getInstance(PageName).put("page", page)
}

fun Util.getPageCount(): Int {
    val page = SPUtils.getInstance(PageName).getInt("page")
    return if (page == -1) 0 else page
}

fun Util.postDelay(delayTime: Long, doSome: () -> Unit) {

    Timer().schedule(
        object : TimerTask() {
            override fun run() {
                doSome()
            }
        },
        delayTime
    )
}

object LogUtil

fun LogUtil.log(msg: String) {
    Log.e("GetContactPerson", msg)
//    LogUtils.eTag("", msg)
}
