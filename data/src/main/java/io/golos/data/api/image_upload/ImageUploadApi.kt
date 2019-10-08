package io.golos.data.api.image_upload

import java.io.File

interface ImageUploadApi {
    fun uploadImage(file: File): String
}