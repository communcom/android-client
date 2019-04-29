package io.golos.domain.model

import java.io.File

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-29.
 */
data class ImageUploadRequest(val imageFile: File) : Identifiable {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id

    inner class Id : Identifiable.Id() {
        val _imageFile = imageFile
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_imageFile != other._imageFile) return false

            return true
        }

        override fun hashCode(): Int {
            return _imageFile.hashCode()
        }

    }
}