package io.golos.domain.post.post_dto

class PostFormatVersion(val major: Int, val minor: Int) {
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