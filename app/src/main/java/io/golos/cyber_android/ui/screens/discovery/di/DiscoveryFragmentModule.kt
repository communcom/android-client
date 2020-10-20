package io.golos.cyber_android.ui.screens.discovery.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.UserIdDomain

@Module
class DiscoveryFragmentModule(
    private val userIdDomain: UserIdDomain
) {

    @Provides
    fun providesUserIDDomain():UserIdDomain = userIdDomain
}
