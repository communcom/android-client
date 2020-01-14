package io.golos.cyber_android.ui.shared.utils

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.view.WindowManager
import android.widget.Toast
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.ImageViewerActivity
import timber.log.Timber

fun Context.shareMessage(string: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, string)
        type = "text/plain"
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Timber.e("Share apps not found")
        Toast.makeText(this, getString(R.string.share_apps_not_found), Toast.LENGTH_LONG).show()
    }
}

fun Context.openImageView(imageUri: Uri) {
    startActivity(ImageViewerActivity.getIntent(this, imageUri.toString()))
}

fun Context.openLinkView(link: Uri) {
    Intent(Intent.ACTION_VIEW, link)
        .also { intent ->
            if (intent.resolveActivity(this.packageManager) != null) {
                startActivity(intent)
            }
        }
}

fun Context.getScreenSize(): Point {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    val display = windowManager?.defaultDisplay
    val screenSize = Point()
    display?.getSize(screenSize)
    return screenSize
}