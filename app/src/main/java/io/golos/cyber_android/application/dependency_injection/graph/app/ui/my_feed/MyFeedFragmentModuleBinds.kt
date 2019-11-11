package io.golos.cyber_android.application.dependency_injection.graph.app.ui.my_feed

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.common.recycler_view.versioned.VersionedListItem
import io.golos.cyber_android.ui.screens.postslist.PostsListModel
import io.golos.cyber_android.ui.screens.postslist.PostsListModelImpl
import io.golos.cyber_android.ui.screens.postslist.PostsListViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.use_cases.posts.GetPostsUseCase
import io.golos.domain.use_cases.posts.GetPostsUseCaseImpl

@Module
interface MyFeedFragmentModuleBinds {

    @Binds
    @ViewModelKey(PostsListViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: PostsListViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: PostsListModelImpl): PostsListModel

    @Binds
    fun bindPaginatorSubscriptions(impl: Paginator.Store<VersionedListItem>): Paginator.Store<VersionedListItem>

    @Binds
    fun bindGetPostsUseCase(getPostsUseCaseImpl: GetPostsUseCaseImpl): GetPostsUseCase
}