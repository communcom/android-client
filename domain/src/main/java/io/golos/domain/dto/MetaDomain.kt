package io.golos.domain.dto

import java.util.*

data class MetaDomain(
    val creationTime: Date,
    val trxId:String?
){
    val browseUrl:String
        get() = "https://explorer.cyberway.io/trx/$trxId"
}