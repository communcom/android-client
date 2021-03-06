package io.golos.cyber_android.ui.shared.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import java.io.File
import java.util.*


private const val PROXY_PATH = "proxy/"
private const val IMAGES_PATH = "images/"
private const val URL_IMG_COMMUN_COM = "img.commun.com"
private const val DEFAULT_IMAGE_SCREEN_SIZE = "640x0/"

fun Uri.localSize(): Point {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(File(this.toString()).absolutePath, options)
    return Point(options.outWidth, options.outHeight)
}

fun Uri.prefetchScreenSize(context: Context): Uri{
    if(this.toString().toLowerCase(Locale.getDefault()).endsWith(".gif")) {
        return this
    }

    val screenSize = context.getScreenSize()
    val width = screenSize.x
    val urlString = this.toString()
    return if(urlString.contains(URL_IMG_COMMUN_COM, true)){
        val imageSizePath = if(width > 0){
            "${width}x0/"
        } else{
            DEFAULT_IMAGE_SCREEN_SIZE
        }
        return when {
            urlString.contains(PROXY_PATH) -> {
                val beforeProxy = urlString.substringBefore(PROXY_PATH)
                val afterProxy = urlString.substringAfter(PROXY_PATH)
                val newUrlString = beforeProxy + PROXY_PATH + imageSizePath + afterProxy
                Uri.parse(newUrlString)
            }
            urlString.contains(IMAGES_PATH) -> {
                val beforeImages = urlString.substringBefore(IMAGES_PATH)
                val afterImages = urlString.substringAfter(IMAGES_PATH)
                val newUrlString = beforeImages + IMAGES_PATH + imageSizePath + afterImages
                Uri.parse(newUrlString)
            }
            else -> this
        }
    } else{
        this
    }
}