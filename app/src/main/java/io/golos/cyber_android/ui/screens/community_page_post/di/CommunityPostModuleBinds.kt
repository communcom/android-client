package io.golos.cyber_android.ui.screens.community_page_post.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page_post.model.CommunityPostModel
import io.golos.cyber_android.ui.screens.community_page_post.model.CommunityPostModelImpl
import io.golos.cyber_android.ui.screens.community_page_post.view_model.CommunityPostViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.SubscribeToCommunityUseCaseImpl
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCase
import io.golos.domain.use_cases.community.UnsubscribeToCommunityUseCaseImpl
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCaseImpl

@Module
interface CommunityPostModuleBinds {

    @Binds
    @ViewModelKey(CommunityPostViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: CommunityPostViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: CommunityPostModelImpl): CommunityPostModel

    @Binds
    fun bindPaginatorSubscriptions(impl: Paginator.Store<VersionedListItem>): Paginator.Store<VersionedListItem>

    @Binds
    fun bindGetPostsUseCase(getPostsUseCaseImpl: GetPostsUseCaseImpl): GetPostsUseCase

    @Binds
    fun bindSubscribeToCommunityUseCase(subscribeToCommunityUseCase: SubscribeToCommunityUseCaseImpl): SubscribeToCommunityUseCase

    @Binds
    fun bindUnsubscribeToCommunityUseCaseImpl(unsubscribeToCommunityUseCase: UnsubscribeToCommunityUseCaseImpl): UnsubscribeToCommunityUseCase
}