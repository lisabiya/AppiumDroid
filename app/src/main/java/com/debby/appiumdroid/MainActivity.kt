package com.debby.appiumdroid

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.decorView.background = ContextCompat.getDrawable(this, R.drawable.bg_white)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestStorage()
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

        btConfirm.setOnClickListener {
            var delayTime = 2;
            val num = etNumber.text.toString()
            try {
                delayTime = num.toInt()
            } catch (e: Exception) {

            }
            GetContactPersonInfoService.delayTime = delayTime
            GetContactPersonInfoService.reset()
            ToastUtils.showShort("设置延迟成功")
        }

        btStop.setOnClickListener {
            val manager = getSystemService(AccessibilityService.ACTIVITY_SERVICE) as ActivityManager
            manager.killBackgroundProcesses("me.weishu.leoric:resident")
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

    private fun requestStorage() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                }

                override fun onDenied(
                    permissionsDeniedForever: List<String>,
                    permissionsDenied: List<String>
                ) {
                    ToastUtils.showShort("需要存储权限")
                }
            })
            .theme { activity -> ScreenUtils.setFullScreen(activity) }
            .request()
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
