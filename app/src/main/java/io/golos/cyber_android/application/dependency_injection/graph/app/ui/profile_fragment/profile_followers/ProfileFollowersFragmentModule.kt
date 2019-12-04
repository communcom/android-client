package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_followers

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.FollowersFilter

@Module
class ProfileFollowersFragmentModule(private val filter: FollowersFilter) {
    @Provides
    fun provideFollowersFilter(): FollowersFilter = filter
}