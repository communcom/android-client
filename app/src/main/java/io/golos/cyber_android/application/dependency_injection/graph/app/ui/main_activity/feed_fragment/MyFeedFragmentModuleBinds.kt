package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.screens.main_activity.feed.UserSubscriptionsFeedViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.interactors.user.UserMetadataUseCase

@Module
abstract class MyFeedFragmentModuleBinds {
    @Binds
    abstract fun provideUserSubscriptionsFeedUseCase(useCase: UserSubscriptionsFeedUseCase): UserSubscriptionsFeedUseCase

    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCase): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(UserSubscriptionsFeedViewModel::class)
    internal abstract fun provideCommunityFeedViewModel(viewModel: UserSubscriptionsFeedViewModel): ViewModel
}