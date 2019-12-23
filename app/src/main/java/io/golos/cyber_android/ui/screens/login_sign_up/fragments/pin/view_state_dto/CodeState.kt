package io.golos.cyber_android.ui.screens.login_sign_up.fragments.pin.view_state_dto

data class CodeState (
    val isPrimaryCodeActive: Boolean,
    val isRepeatedCodeActive: Boolean,
    val isInErrorState: Boolean,
    val resetNeeded: Boolean
)