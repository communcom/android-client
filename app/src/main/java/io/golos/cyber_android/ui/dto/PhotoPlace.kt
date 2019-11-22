package io.golos.cyber_android.ui.dto

enum class PhotoPlace(val value: Int) {
    COVER(0),
    AVATAR(1);

    companion object {
        fun create(value: Int) = values().first { it.value == value }
    }
}