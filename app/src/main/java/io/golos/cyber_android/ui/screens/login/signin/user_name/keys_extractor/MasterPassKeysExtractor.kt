package io.golos.cyber_android.ui.screens.login.signin.keys_extractors.master_pass

import io.golos.domain.requestmodel.AuthRequestModel
import io.golos.sharedmodel.Either

interface MasterPassKeysExtractor {
   suspend fun process(userName: String, masterKey: String): Either<AuthRequestModel, Exception>
}