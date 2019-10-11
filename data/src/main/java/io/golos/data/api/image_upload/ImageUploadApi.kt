package io.golos.data.api.image_upload

import android.net.Uri
import java.io.File

interface ImageUploadApi {
    /**
     * @param localUri - we need it only for mock
     * @return Uri of uploaded image on server
     */
    suspend fun uploadImage(file: File, localUri: Uri): String
}