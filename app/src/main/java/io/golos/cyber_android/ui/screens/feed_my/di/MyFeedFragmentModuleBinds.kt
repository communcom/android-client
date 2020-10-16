package io.golos.cyber_android.ui.screens.feed_my.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.feed_my.model.MyFeedModel
import io.golos.cyber_android.ui.screens.feed_my.model.MyFeedModelImpl
import io.golos.cyber_android.ui.screens.feed_my.view_model.MyFeedViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCaseImpl
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCaseImpl
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCaseImpl
import io.golos.domain.use_cases.user.GetLocalUserUseCase
import io.golos.domain.use_cases.user.GetLocalUserUseCaseImpl

@Module
interface MyFeedFragmentModuleBinds {
    @Binds
    @ViewModelKey(MyFeedViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: MyFeedViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: MyFeedModelImpl): MyFeedModel

    @Binds
    fun bindGetPostsUseCase(getPostsUseCaseImpl: GetPostsUseCaseImpl): GetPostsUseCase

    @Binds
    fun bindGetLocalUserUseCase(getLocalUserUseCase: GetLocalUserUseCaseImpl): GetLocalUserUseCase

    @Binds
    fun bindSubscribeToCommunityUseCase(subscribeToCommunityUseCase: SubscribeToCommunityUseCaseImpl): SubscribeToCommunityUseCase

    @Binds
    fun bindUnsubscribeToCommunityUseCaseImpl(unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCaseImpl): UnsubscribeToCommunityUseCase
}