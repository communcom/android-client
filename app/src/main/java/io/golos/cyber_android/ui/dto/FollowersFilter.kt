package io.golos.cyber_android.ui.dto

enum class FollowersFilter(val value: Int) {
    ALL(0),
    MUTUAL(1),
    FOLLOWERS(2),
    FOLLOWING(3);

    companion object {
        fun create(value: Int) = values().first { it.value == value }
        fun create(value: String) = values().first { it.toString() == value }
    }
}