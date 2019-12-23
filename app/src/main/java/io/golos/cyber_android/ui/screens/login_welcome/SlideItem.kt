package io.golos.cyber_android.ui.screens.login_welcome

import android.text.Spannable
import androidx.annotation.DrawableRes

data class SlideItem(
    @DrawableRes val img: Int,
    val text: Spannable,
    val subText: Spannable
)