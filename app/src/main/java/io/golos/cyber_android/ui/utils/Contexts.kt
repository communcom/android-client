package io.golos.cyber_android.ui.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.golos.cyber_android.R
import timber.log.Timber


fun Context.shareMessage(string: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, string)
        type = "text/plain"
    }
    startActivity(intent)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Timber.e("Share apps not found")
        Toast.makeText(this, getString(R.string.share_apps_not_found), Toast.LENGTH_LONG).show()
    }
}