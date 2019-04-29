package io.golos.cyber_android.ui.screens.profile.edit.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class NotificationSetting(@DrawableRes val icon: Int, @StringRes val title: Int, var isEnabled: Boolean = true)
