package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.main_activity.feed.UserSubscriptionsFeedViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCase
import io.golos.domain.interactors.feed.UserSubscriptionsFeedUseCaseImpl
import io.golos.domain.interactors.user.UserMetadataUseCase
import io.golos.domain.interactors.user.UserMetadataUseCaseImpl

@Module
abstract class MyFeedFragmentModuleBinds {
    @Binds
    abstract fun provideUserSubscriptionsFeedUseCase(useCase: UserSubscriptionsFeedUseCaseImpl): UserSubscriptionsFeedUseCase

    @Binds
    abstract fun provideUserMetadataUseCase(useCase: UserMetadataUseCaseImpl): UserMetadataUseCase

    @Binds
    @FragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(UserSubscriptionsFeedViewModel::class)
    abstract fun provideCommunityFeedViewModel(viewModel: UserSubscriptionsFeedViewModel): ViewModel
}