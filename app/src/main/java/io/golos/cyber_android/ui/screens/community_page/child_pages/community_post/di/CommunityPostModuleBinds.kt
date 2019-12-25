package io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model.CommunityPostModel
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.model.CommunityPostModelImpl
import io.golos.cyber_android.ui.screens.community_page.child_pages.community_post.view_model.CommunityPostViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
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

}