package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.CommunitiesRepository
import io.golos.cyber_android.ui.screens.main_activity.communities.data_repository.CommunitiesRepositoryImpl
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class CommunitiesFragmentModuleBinds {
    @Binds
    @FragmentScope
    abstract fun bindCommunitiesRepository(repository: CommunitiesRepositoryImpl): CommunitiesRepository
}