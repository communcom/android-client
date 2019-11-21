package io.golos.cyber_android.ui.screens.profile.new_profile.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.dto.UserProfileDomain

interface ProfileModel: ModelBase {
    suspend fun loadProfileInfo(): UserProfileDomain
}