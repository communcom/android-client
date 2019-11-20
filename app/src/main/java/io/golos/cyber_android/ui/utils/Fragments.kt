package io.golos.cyber_android.ui.utils

import androidx.fragment.app.Fragment
import io.golos.cyber_android.ui.common.base.ActivityBase

fun Fragment.setFullScreenMode(){
    val requireActivity = requireActivity()
    if(requireActivity is ActivityBase){
        requireActivity.setFullScreenMode()
    }
}

fun Fragment.clearFullScreenMode(){
    val requireActivity = requireActivity()
    if(requireActivity is ActivityBase){
        requireActivity.clearFullScreenMode()
    }
}