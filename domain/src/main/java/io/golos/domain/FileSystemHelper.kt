package io.golos.domain

import android.net.Uri
import io.golos.domain.utils.IdUtil
import java.io.File

interface FileSystemHelper {
    /**
     * Copy file to a temporary junk folder
     * @return url to a new file or null in case of error
     */
    suspend fun copyImageToJunkFolder(sourceUri: Uri?): Uri?

    fun getTempImageFile(): File
}