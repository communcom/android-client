package io.golos.domain.posts_parsing_rendering.post_metadata.post_dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DocumentFormatVersion(val major: Int, val minor: Int): Parcelable {
    companion object {
        fun parse(textVersion: String): DocumentFormatVersion =
            textVersion.split(".")
                .let {
                    DocumentFormatVersion(
                        it[0].toInt(),
                        it[1].toInt()
                    )
                }
    }

    override fun toString(): String {
        return "$major.$minor"
    }
}