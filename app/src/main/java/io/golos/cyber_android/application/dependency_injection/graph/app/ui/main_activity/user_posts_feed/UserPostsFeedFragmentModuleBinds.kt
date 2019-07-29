package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.posts.UserPostsFeedViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.feed.UserPostFeedUseCase

@Module
abstract class UserPostsFeedFragmentModuleBinds {
    @Binds
    abstract fun provideUserPostFeedUseCase(useCase: UserPostFeedUseCase): UserPostFeedUseCase

    @Binds
    @IntoMap
    @ViewModelKey(UserPostsFeedViewModel::class)
    internal abstract fun provideUserPostsFeedViewModel(viewModel: UserPostsFeedViewModel): ViewModel

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory
}