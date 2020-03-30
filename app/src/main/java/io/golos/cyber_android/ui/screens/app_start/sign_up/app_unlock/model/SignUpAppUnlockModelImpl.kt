package io.golos.cyber_android.ui.screens.app_start.sign_up.app_unlock.model

import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.model.AppUnlockModel
import io.golos.domain.dto.AppUnlockWay
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.UnlockMethodSelected
import javax.inject.Inject

class SignUpAppUnlockModelImpl
@Inject
constructor(
    private val signUpCore: SignUpCoreView
) : AppUnlockModel {

    /**
     * @return true in case of success
     */
    override suspend fun saveAppUnlockWay(unlockWay: AppUnlockWay) = signUpCore.process(UnlockMethodSelected(unlockWay))
}