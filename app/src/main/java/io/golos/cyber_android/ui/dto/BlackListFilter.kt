package io.golos.cyber_android.ui.dto

enum class BlackListFilter(val value: Int) {
    COMMUNITIES(0),
    USERS(1);

    companion object {
        fun create(value: Int) = values().first { it.value == value }
        fun create(value: String) = values().first { it.toString() == value }
    }
}