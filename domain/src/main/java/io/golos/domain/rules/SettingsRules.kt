package io.golos.domain.rules

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import io.golos.domain.DefaultSettingProvider
import io.golos.domain.DeviceIdProvider
import io.golos.domain.entities.GeneralSettingEntity
import io.golos.domain.entities.NSFWSettingsEntity
import io.golos.domain.entities.NotificationSettingsEntity
import io.golos.domain.entities.UserSettingEntity
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
class MyDefaultSettingProvider
@Inject
constructor() : DefaultSettingProvider {
    override fun provide(): UserSettingEntity {
        return UserSettingEntity(
            GeneralSettingEntity(NSFWSettingsEntity.ALERT_WARN, "en"),
            NotificationSettingsEntity(
                true, true, true, true, true,
                true, true, true, true, true, true,
                true
            )
        )
    }
}

@SuppressLint("HardwareIds")
class MyDeviceIdProvider
@Inject
constructor(private val context: Context) : DeviceIdProvider {
    private val fileName = "device_id.txt"

    private val deviceId by lazy {
        var deviceId = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        if (deviceId == null) File(context.filesDir, "device_id.txt")?.bufferedReader()?.readLine()

        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString()
            val idFile = File(context.filesDir, "device_id.txt")
            idFile.bufferedWriter().apply {
                write(deviceId)
                flush()
            }

        }

        deviceId
    }

    override fun provide(): String {
        return deviceId
    }
}

