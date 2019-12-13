package io.golos.cyber_android.ui.dto

enum class FollowersFilter(val value: Int) {
    MUTUALS(0),
    FOLLOWERS(1),
    FOLLOWINGS(2);

    companion object {
        fun create(value: Int) = values().first { it.value == value }
        fun create(value: String) = values().first { it.toString() == value }
    }
}