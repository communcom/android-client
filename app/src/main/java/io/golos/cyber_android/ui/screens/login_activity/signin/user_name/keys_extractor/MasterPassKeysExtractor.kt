package io.golos.cyber_android.ui.screens.login_activity.signin.user_name.keys_extractor

import io.golos.cyber4j.sharedmodel.Either
import io.golos.domain.requestmodel.AuthRequestModel

interface MasterPassKeysExtractor {
   suspend fun process(userName: String, masterKey: String): Either<AuthRequestModel, Exception>
}