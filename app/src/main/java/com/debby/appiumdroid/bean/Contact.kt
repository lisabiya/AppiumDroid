package com.debby.appiumdroid.bean

import org.litepal.crud.LitePalSupport

/**
 * Create by wakfu on 2020/4/15
 * 联系人列表
 */
class Contact() : LitePalSupport() {
    var nickName: String? = null
    var company: String? = null
    var phone: String? = null
    var values: String? = null
}