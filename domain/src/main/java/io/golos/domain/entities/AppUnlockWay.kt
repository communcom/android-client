package io.golos.domain.entities

enum class AppUnlockWay(val value: Int) {
    FINGERPRINT(1),
    PIN_CODE(2);

    companion object {
        fun createFromValue(value: Int) = values().first { it.value == value }
    }
}