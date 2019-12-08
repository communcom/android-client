package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_followers

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserDomain
import javax.inject.Named

@Module
class ProfileFollowersFragmentModule(
    private val filter: FollowersFilter,
    private val pageSize: Int,
    private val mutualUsers: List<UserDomain>) {

    @Provides
    fun provideFollowersFilter(): FollowersFilter = filter

    @Provides
    @Named(Clarification.PAGE_SIZE)
    fun providePageSize(): Int = pageSize

    @Provides
    fun provideMutualUsers(): List<UserDomain> = mutualUsers
}