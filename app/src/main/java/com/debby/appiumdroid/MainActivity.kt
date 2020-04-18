package com.debby.appiumdroid

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        this.window.decorView.background = ContextCompat.getDrawable(this, R.drawable.bg_white)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestStorage()
        initView()
    }

    private fun initView() {
        btStart.setOnClickListener {
            //            settingAccessibilityInfo()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }
        btReset.setOnClickListener {
            GetContactPersonInfoService.reset()
            ToastUtils.showShort("重置数据成功")
//            settingAccessibilityInfo()
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

        btEnd.setOnClickListener {
            var maxRow = 6;
            val num = etEnd.text.toString()
            try {
                maxRow = num.toInt()
            } catch (e: Exception) {

            }
            GetContactPersonInfoService.maxRow = maxRow
            GetContactPersonInfoService.reset()
            ToastUtils.showShort("设置页面条数成功")
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

        btDing.setOnClickListener {
            val packageManager = packageManager;
            intent = packageManager.getLaunchIntentForPackage("com.alibaba.android.rimet");
            if (intent == null) {
                ToastUtils.showShort("未安装")
            } else {
                startActivity(intent);
            }
        }

        stPage.setOnCheckedChangeListener { _, isChecked ->
            GetContactPersonInfoService.bolSkip = isChecked;
            stPage.text = if (isChecked) "开启" else "关闭"
            if (isChecked) {
                var page = 2;
                val num = etPage.text.toString()
                try {
                    page = num.toInt()
                } catch (e: Exception) {

                }
                GetContactPersonInfoService.skipPage = page;
            }
        }

        tvPage.text = String.format("上级记录到第%d页", Util.getPageCount())

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
