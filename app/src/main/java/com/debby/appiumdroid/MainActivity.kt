package com.debby.appiumdroid

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btStart.setOnClickListener {
            //            settingAccessibilityInfo()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
        btReset.setOnClickListener {
            GetContactPersonInfoService.reset()
            ToastUtils.showShort("重置数据成功")
        }
        btClear.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("确定清空爬取的数据？")
                .setPositiveButton("坚持清除") { _, _ ->
                    SPUtils.getInstance(SpName).clear()
                    ToastUtils.showShort("清除数据成功")
                }.setNegativeButton("取消", null)
                .show()
        }

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_list -> {
                    ListActivity.goTo(this)
                }
                else -> {
                }
            }
            true
        }
    }

//    private fun settingAccessibilityInfo() {
//        val packageNames =
//            arrayOf("com.alibaba.android.rimet")
//        val mAccessibilityServiceInfo = AccessibilityServiceInfo()
//        // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
//        mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
//        // 反馈给用户的类型，这里是语音提示
//        mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
//        // 过滤的包名
//        mAccessibilityServiceInfo.packageNames = packageNames
//        GetContactPersonInfoService().serviceInfo = mAccessibilityServiceInfo
//    }
}
