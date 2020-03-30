package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.dto

data class CodeState (
    val isPrimaryCodeActive: Boolean,
    val isRepeatedCodeActive: Boolean,
    val resetNeeded: Boolean
)