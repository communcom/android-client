package io.golos.cyber_android.ui.shared.utils

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import io.golos.cyber_android.ui.shared.base.ActivityBase
import timber.log.Timber


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

fun Fragment.openWebPage(url: String) {
    try {
        val webPage = Uri.parse(url)
        val myIntent = Intent(Intent.ACTION_VIEW, webPage)
        startActivity(myIntent)
    } catch (e: ActivityNotFoundException) {
        Timber.e(e)
    }

}