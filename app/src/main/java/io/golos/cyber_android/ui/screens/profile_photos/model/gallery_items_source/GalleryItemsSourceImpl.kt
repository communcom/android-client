package io.golos.cyber_android.ui.screens.profile_photos.model.gallery_items_source

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.util.*
import javax.inject.Inject

class GalleryItemsSourceImpl
@Inject
constructor(
    private val appContext: Context
) : GalleryItemsSource {
    /**
     * Returns list of images in the phone gallery, sorted by lastItemDate in descending order
     */
    override fun getGalleryImagesUrls(): List<String> = getGalleryItems().sortedByDescending { it.second }.map { it.first }

    @SuppressLint("Recycle")
    private fun getGalleryItems(): List<Pair<String, Date>> {
        val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.MIME_TYPE)

        appContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)!!.use { cursor ->
            val result = mutableListOf<Pair<String, Date>>()

            if (cursor.moveToFirst()) {
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

                do {
                    val mimeType = cursor.getString(mimeTypeColumn)
                    if(mimeType == "image/gif") {   // GIFs are not supported
                        continue
                    }

                    val data = cursor.getString(dataColumn)
                    val dateAdded = cursor.getString(dateAddedColumn)
                    result.add(Pair(Uri.fromFile(File(data)).toString(), Date(dateAdded.toLong())))
                } while (cursor.moveToNext())
            }

            return result
        }
    }
}