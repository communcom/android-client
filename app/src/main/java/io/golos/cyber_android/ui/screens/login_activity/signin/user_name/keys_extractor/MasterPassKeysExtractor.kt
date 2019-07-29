package io.golos.cyber_android.ui.screens.login_activity.signin.user_name.keys_extractor

import io.golos.domain.requestmodel.AuthRequestModel
import io.golos.sharedmodel.Either

interface MasterPassKeysExtractor {
   suspend fun process(userName: String, masterKey: String): Either<AuthRequestModel, Exception>
}