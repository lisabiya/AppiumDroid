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

/**
 * Create by wakfu on 2020/4/13
 */
class GetContactPersonInfoService : AccessibilityService() {
    companion object {
        var record = 1
        var page = 1

        fun reset() {
            record = 1
            page = 1
        }
    }


    private var parents: List<AccessibilityNodeInfo>? = null
    private val mMainHandler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        parents = ArrayList()
        LogUtil.log("onServiceConnected: ")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.className == null) return
        val className = event.className.toString()
        if (!className.startsWith("com.alibaba.android.user")) {
            return
        }

        LogUtil.log("onAccessibilityEvent: $className")

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> when (className) {
                "com.alibaba.android.user.external.list.ExternalListActivity" -> Util.postDelay(500) {
                    scanListRecord("com.alibaba.android.rimet:id/list_view")
                }
                "com.alibaba.android.user.profile.namecard.UserBusinessProfileActivityV2" ->
                    Util.postDelay(500) {
                        val pack = "com.alibaba.android.rimet:id"
                        val list = arrayListOf(
                            Pair("昵称", "$pack/user_header_full_name"),
                            Pair("公司", "$pack/item_user_profile_cell_title_tv"),
                            Pair("电话", "$pack/user_mobile_info_content_tv"),
                            Pair("标题", "$pack/cell_title"),
                            Pair("名称", "$pack/cell_subTitle")
                        )
                        getTextInfo(list)
                        record++
                        inputClickBack()
                    }
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
                nodeInfo.findAccessibilityNodeInfosByViewId("com.alibaba.android.rimet:id/ui_common_base_ui_activity_toolbar")
            for (item in list) {
                val child = item.getChild(0)
                if (child.contentDescription == "返回") {
                    LogUtil.log("inputClickBack: " + "返回")
                    child.performAction(AccessibilityNodeInfo.ACTION_CLICK)
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
        LogUtil.log("当前页面: $record")
        if (count > record) {
            val childInfo = info.getChild(record)
//            childInfo.findAccessibilityNodeInfosByViewId()
            childInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            if (count < 8) {
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
            val map = ArrayList<Pair<String, String>>()
            for (pair in pairList) {
                val list = nodeInfo.findAccessibilityNodeInfosByViewId(pair.second)
                for (item in list) {
                    map.add(Pair(pair.first, item.text.toString()))
                }
            }
            Util.setData(map[0].second!!, map)

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
            Util.postDelay(1500) {
                scanListRecord("com.alibaba.android.rimet:id/list_view")
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