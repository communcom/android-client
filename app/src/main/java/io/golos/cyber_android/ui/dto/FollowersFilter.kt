package io.golos.cyber_android.ui.dto

enum class FollowersFilter(val value: Int) {
    MUTUAL(0),
    FOLLOWERS(1),
    FOLLOWING(2);

    companion object {
        fun create(value: Int) = values().first { it.value == value }
        fun create(value: String) = values().first { it.toString() == value }
    }
}