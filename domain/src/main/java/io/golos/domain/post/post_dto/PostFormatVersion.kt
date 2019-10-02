package io.golos.domain.post.post_dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PostFormatVersion(val major: Int, val minor: Int): Parcelable {
    companion object {
        fun parse(textVersion: String): PostFormatVersion =
            textVersion.split(".")
                .let {
                    PostFormatVersion(it[0].toInt(), it[1].toInt())
                }
    }

    override fun toString(): String {
        return "$major.$minor"
    }
}