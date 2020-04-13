package io.golos.cyber_android.services.firebase.notifications.custom_json_adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.golos.utils.format.DatesServerFormatter
import java.util.*

class DateJsonAdapter {
    @ToJson
    fun toJson(data: Date): String = DatesServerFormatter.formatToServer(data)

    @FromJson
    fun fromJson(data: String): Date = DatesServerFormatter.formatFromServer(data)
}