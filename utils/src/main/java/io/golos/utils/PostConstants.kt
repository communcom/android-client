package io.golos.utils

object PostConstants {
    const val MAX_POST_TITLE_LENGTH = 256
    const val MAX_POST_CONTENT_LENGTH = 4096

    // If post text length is greater than this value the post must be marked as long read
    const val POST_LONG_READ_THRESHOLD = 2048

    const val MAX_COMMENT_CONTENT_LENGTH = 4096
}