package io.golos.cyber_android.ui.shared.utils

import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File


fun Uri.toBitmapOptions(): BitmapFactory.Options {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(File(path).absolutePath, options)
    return options
}