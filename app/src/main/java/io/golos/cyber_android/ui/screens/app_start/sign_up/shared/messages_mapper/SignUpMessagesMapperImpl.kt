package io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.use_cases.sign_up.core.data_structs.SignUpMessageCode
import javax.inject.Inject

class SignUpMessagesMapperImpl
@Inject
constructor(
    private val appContext: Context
) : SignUpMessagesMapper {
    override fun getMessage(code: SignUpMessageCode): String {
        val resId = when(code) {
            SignUpMessageCode.TOKEN_RECEIVING_ERROR -> R.string.token_receiving_error
            SignUpMessageCode.ACCOUNT_ALREADY_REGISTERED -> R.string.account_registered_error
            SignUpMessageCode.PHONE_ALREADY_REGISTERED -> R.string.phone_already_taken_error
            SignUpMessageCode.GENERAL_ERROR -> R.string.common_general_error
            SignUpMessageCode.USERNAME_ALREADY_TAKEN -> R.string.name_already_taken_error
            SignUpMessageCode.PASSWORDS_ARE_NOT_SAME -> R.string.password_not_match_error
            SignUpMessageCode.WRITE_TO_BLOCK_CHAIN_ERROR -> R.string.write_to_blockchain_error
            SignUpMessageCode.AUTH_ERROR -> R.string.auth_error
            SignUpMessageCode.INVALID_PHONE_VERIFICATION_CODE -> R.string.wrong_verification_code_error
            SignUpMessageCode.TOO_MANY_RETRIES -> R.string.too_many_retries_error
            SignUpMessageCode.TRY_LATER -> R.string.try_later_error
            SignUpMessageCode.PHONE_VERIFICATION_CODE_RESENT  -> R.string.verification_code_resent
        }

        return appContext.getString(resId)
    }
}