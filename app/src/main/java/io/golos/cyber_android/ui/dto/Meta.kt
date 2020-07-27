package io.golos.cyber_android.ui.dto

import java.util.*

data class Meta(
    val creationTime: Date,
    val trxId:String?
){
    val browseUrl:String
        get() = "https://explorer.cyberway.io/trx/$trxId"
}