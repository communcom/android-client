package io.golos.cyber_android.ui.screens.app_start.welcome.welcome_fragment

import android.text.Spannable
import androidx.annotation.DrawableRes

data class SlideItem(
    @DrawableRes val img: Int,
    val text: Spannable,
    val subText: Spannable
)