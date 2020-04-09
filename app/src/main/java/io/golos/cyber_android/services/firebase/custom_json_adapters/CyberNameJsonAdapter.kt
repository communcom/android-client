package io.golos.cyber_android.services.firebase.custom_json_adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.golos.commun4j.sharedmodel.CyberName

class CyberNameJsonAdapter {
    @ToJson
    fun toJson(data: CyberName): String = data.name

    @FromJson
    fun fromJson(data: String): CyberName = CyberName(data)
}