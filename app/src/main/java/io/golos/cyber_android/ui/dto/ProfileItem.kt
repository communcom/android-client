package io.golos.cyber_android.ui.dto

enum class ProfileItem(val value: Int) {
    COVER(0),
    AVATAR(1),
    BIO(2),
    COMMENT(3);

    companion object {
        fun create(value: Int) = values().first { it.value == value }
    }
}