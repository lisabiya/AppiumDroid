package com.debby.appiumdroid

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import androidx.appcompat.app.AppCompatActivity
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
            GetContactPersonInfoService.record = 1;
            GetContactPersonInfoService.page = 1;
        }
    }

    private fun settingAccessibilityInfo() {
        val packageNames =
            arrayOf("com.alibaba.android.rimet")
        val mAccessibilityServiceInfo = AccessibilityServiceInfo()
        // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
        mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        // 反馈给用户的类型，这里是语音提示
        mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
        // 过滤的包名
        mAccessibilityServiceInfo.packageNames = packageNames
        GetContactPersonInfoService().serviceInfo = mAccessibilityServiceInfo
    }
}
