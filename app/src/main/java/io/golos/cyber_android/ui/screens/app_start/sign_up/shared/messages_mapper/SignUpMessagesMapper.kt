package io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper

import io.golos.use_cases.sign_up.core.data_structs.SignUpMessageCode

interface SignUpMessagesMapper {
    fun getMessage(code: SignUpMessageCode): String
}