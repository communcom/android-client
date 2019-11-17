package io.golos.domain.use_cases.post.post_dto

enum class PostType(val value: Int) {
    BASIC(0),
    ARTICLE(1),
    COMMENT(2);

    companion object {
        fun create(value: Int): PostType = values().first { it.value == value}
    }
}