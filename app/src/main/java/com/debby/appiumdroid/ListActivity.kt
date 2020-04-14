package com.debby.appiumdroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.JsonUtils
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_list.*


class ListActivity : AppCompatActivity() {
    companion object {
        fun goTo(activity: AppCompatActivity) {
            val intent = Intent(activity, ListActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val stringBuilder = StringBuilder()
        val map = SPUtils.getInstance(SpName).all

        stringBuilder.append("[")
        for (mutableEntry in map) {
            val json = mutableEntry.value.toString()
            stringBuilder.append(JsonUtils.formatJson(json))
            stringBuilder.append(",")
        }
        stringBuilder.append("]")
        stringBuilder.replace(stringBuilder.length - 2, stringBuilder.length - 1, "")
        tvMsg.setText(stringBuilder.toString())
    }

}
