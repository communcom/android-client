package io.golos.data.api

import java.io.File

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
interface ImageUploadApi {
    fun uploadImage(file: File): String
}