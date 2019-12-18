package io.golos.cyber_android.ui.screens.login_sign_in_old.user_name.keys_extractor

import io.golos.commun4j.sharedmodel.Either
import io.golos.domain.requestmodel.AuthRequestModel

interface MasterPassKeysExtractor {
   suspend fun process(userName: String, masterKey: String): Either<AuthRequestModel, Exception>
}