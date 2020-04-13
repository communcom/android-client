package io.golos.cyber_android.services.firebase.notifications.custom_json_adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.golos.commun4j.sharedmodel.CyberSymbolCode

class CyberSymbolCodeJsonAdapter {
    @ToJson
    fun toJson(data: CyberSymbolCode): String = data.value

    @FromJson
    fun fromJson(data: String): CyberSymbolCode = CyberSymbolCode(data)
}