package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.user_posts_feed

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile.posts.UserPostsFeedViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.dto.PostEntity
import io.golos.domain.use_cases.feed.AbstractFeedUseCase
import io.golos.domain.use_cases.feed.UserPostFeedUseCase
import io.golos.domain.use_cases.model.PostModel
import io.golos.domain.requestmodel.PostFeedUpdateRequest

@Module
abstract class UserPostsFeedFragmentModuleBinds {
    @Binds
    abstract fun provideUserPostFeedUseCase(useCase: UserPostFeedUseCase): AbstractFeedUseCase<PostFeedUpdateRequest, PostEntity, PostModel>

    @Binds
    @IntoMap
    @ViewModelKey(UserPostsFeedViewModel::class)
    internal abstract fun provideUserPostsFeedViewModel(viewModel: UserPostsFeedViewModel): ViewModel

    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory
}