package com.debby.appiumdroid

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.util.Pair
import com.blankj.utilcode.util.GsonUtils
import com.debby.appiumdroid.bean.Contact
import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Create by wakfu on 2020/4/13
 */
class GetContactPersonInfoService : AccessibilityService() {
    companion object {
        var record = 1
        var page = 1
        var delayTime = 1
        var maxRow = 4
        var skipPage = 0
        var bolSkip = false

        const val pack = "com.alibaba.android.rimet:id"

        fun reset() {
            record = 1
            page = 1
        }
    }


    private val mMainHandler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtil.log("onServiceConnected: ")
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.className == null) return
        val className = event.className.toString()
        if (!className.startsWith("com.alibaba.android")) {
            return
        }
        LogUtil.log("onAccessibilityEvent: $className")

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> when (className) {
                "com.alibaba.android.user.external.list.ExternalListActivity" -> {
                    if (bolSkip) {
                        skipPage("$pack/list_view")
                        return
                    }
                    var time = 1L;
                    if (delayTime > 1) {
                        time = Random().nextInt(delayTime) + 1L
                    }
                    Util.postDelay(time * 1000) {
                        //                        inputClickBack()
                        scanListRecord("$pack/list_view")
                    }
                }
                "com.alibaba.android.user.profile.namecard.UserBusinessProfileActivityV2" -> {
                    Util.postDelay(1500) {
                        val list = arrayListOf(
                            Pair("昵称", "$pack/user_header_full_name"),
                            Pair("认证信息", "$pack/profile_tv_org"),
                            Pair("认证标签", "$pack/tv_des"),
                            Pair("公司", "$pack/item_user_profile_cell_title_tv"),
                            Pair("电话", "$pack/user_mobile_info_content_tv"),
                            Pair("标题", "$pack/cell_title"),
                            Pair("名称", "$pack/cell_subTitle"),
                            Pair("标签", "$pack/label_list_layout")
                        )
                        getTextInfo(list)
                        record++
                        inputClickBack()
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                LogUtil.log("TYPE_VIEW_SCROLLED")
            }
            else -> {

            }
        }
    }

    override fun onInterrupt() {}

    /**
     * 通过ID获取控件，并进行模拟点击
     *
     * @param clickId
     */
    private fun inputClick(clickId: String) {
        val nodeInfo = rootInActiveWindow
        if (nodeInfo != null) {
            val list =
                nodeInfo.findAccessibilityNodeInfosByViewId(clickId)
            for (item in list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    /**
     * 模拟点击返回键
     */
    private fun inputClickBack() {
        val nodeInfo = rootInActiveWindow
        if (nodeInfo != null) {
            val list =
                nodeInfo.findAccessibilityNodeInfosByViewId("$pack/ui_common_base_ui_activity_toolbar")

            for (item in list) {
                try {
                    val child = item.getChild(0)
                    val title = item.getChild(1)
                    val isBack = (title == null || title.text != "外部联系人")
                    if (child?.contentDescription == "返回" && isBack) {
                        LogUtil.log("inputClickBack: " + "返回")
                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    }
                } catch (e: java.lang.Exception) {
                    LogUtil.log("inputClickBack: " + e.message)
                }
            }
        }
    }

    /**
     * 遍历列表
     *
     * @param listId 列表ID
     */
    private fun scanListRecord(listId: String) {
        val nodeInfo = rootInActiveWindow
        if (nodeInfo != null) {
            val list =
                nodeInfo.findAccessibilityNodeInfosByViewId(listId)
            for (item in list) {
                gotoDetailPage(item)
            }
        }
    }

    /**
     * 前往详情页
     */
    private fun gotoDetailPage(info: AccessibilityNodeInfo) {
        val count = info.childCount
        LogUtil.log("当前页面: $page-$record")
        if (count > record) {
            val childInfo = info.getChild(record)
            childInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            if (count < maxRow) {
                LogUtil.log("爬取结束")
                showToast("爬取结束")
            } else {
                judgeScroll(info)
            }
        }
    }

    /**
     * 通过ID获取控件，并获取文字
     */
    private fun getTextInfo(pairList: List<Pair<String, String>>) {
        val nodeInfo = rootInActiveWindow
        if (nodeInfo != null) {
            val contact = Contact()
            val titleArray = ArrayList<String?>()
            val valueArray = ArrayList<String?>()
            val tagArray = ArrayList<String?>()
            val values = HashMap<String?, String?>()

            for (pair in pairList) {
                val list = nodeInfo.findAccessibilityNodeInfosByViewId(pair.second)
                for (item in list) {
                    when (pair.first) {
                        "昵称" -> {
                            contact.nickName = item.text?.toString()
                        }
                        "公司" -> {
                            contact.company = item.text?.toString()
                        }
                        "电话" -> {
                            contact.phone = item.text?.toString()
                        }
                        "认证信息" -> {
                            contact.authName = item.text?.toString()
                        }
                        "认证标签" -> {
                            contact.authTag = item.text?.toString()
                        }
                        "标题" -> {
                            titleArray.add(item.text?.toString())
                        }
                        "名称" -> {
                            valueArray.add(item.text?.toString())
                        }
                        "标签" -> {
                            item.childCount
                            for (i in 0 until item.childCount) {
                                tagArray.add(item.getChild(i)?.text?.toString())
                            }
                        }
                    }
                }
            }
            titleArray.forEachIndexed { index, title ->
                try {
                    values[title] = valueArray[index]
                } catch (e: Exception) {
                    LogUtil.log("inputClickBack: " + e.message)
                }
            }
            contact.values = GsonUtils.toJson(values)
            contact.tags = GsonUtils.toJson(tagArray)

            val primaryKey = if (contact.nickName != null) contact.nickName!! else ""
            Util.setData(primaryKey, contact)
            contact.saveOrUpdate("nickName=?", primaryKey)
        }

    }

    /**
     * 判断是否滚动
     */
    private fun judgeScroll(info: AccessibilityNodeInfo) {
        if (info.isScrollable) {
            info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
            LogUtil.log("滚动")
            record = 1
            page++

            Util.setPageCount(page)

            Util.postDelay(1000) {
                scanListRecord("$pack/list_view")
            }
        }
    }


    /**
     * 前往详情页
     */
    private fun skipPage(listId: String) {
        if (page > skipPage) {
            bolSkip = false
            scanListRecord("$pack/list_view")
            return
        }
        val nodeInfo = rootInActiveWindow
        if (nodeInfo != null) {
            val list = nodeInfo.findAccessibilityNodeInfosByViewId(listId)
            for (item in list) {
                val count = item.childCount
                LogUtil.log("跳转中的当前页面: $page-$record")

                if (count < maxRow) {
                    LogUtil.log("爬取结束")
                    showToast("爬取结束")
                } else {
                    if (item.isScrollable) {
                        item.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                        LogUtil.log("滚动")
                        record = 1
                        page++

                        Util.postDelay(1000) {
                            skipPage("$pack/list_view")
                        }
                    }
                }
            }
        }
    }

    /**
     * 滚动屏幕
     */
    private fun scrollScreen(info: AccessibilityNodeInfo) {
        val bounds = Rect()
        info.getBoundsInScreen(bounds)
        val path = Path()
        path.moveTo(200f, bounds.bottom - 20f)
        path.lineTo(200f, bounds.top + 20f)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val gestureDescription = GestureDescription.Builder()
                .addStroke(
                    StrokeDescription(path, 200, 500)
                ).build()
            dispatchGesture(gestureDescription, object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)
                    LogUtil.log("滑动结束")
                }

                override fun onCancelled(gestureDescription: GestureDescription) {
                    super.onCancelled(gestureDescription)
                    LogUtil.log("取消")
                }
            }, Handler(Handler.Callback { false }))
        }
    }

    /**
     * 弹出消息
     *
     * @param msg 消息
     */
    private fun showToast(msg: String) {
        mMainHandler.post {
            Toast.makeText(
                this@GetContactPersonInfoService,
                msg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}