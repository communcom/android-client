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
import io.golos.cyber_android.ui.screens.my_feed.model.MyFeedModel
import io.golos.cyber_android.ui.screens.my_feed.model.MyFeedModelImpl
import io.golos.cyber_android.ui.screens.my_feed.view_model.MyFeedViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
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
    fun bindPaginatorSubscriptions(impl: Paginator.Store<VersionedListItem>): Paginator.Store<VersionedListItem>

    @Binds
    fun bindGetPostsUseCase(getPostsUseCaseImpl: GetPostsUseCaseImpl): GetPostsUseCase

    @Binds
    fun bindGetLocalUserUseCase(getLocalUserUseCase: GetLocalUserUseCaseImpl): GetLocalUserUseCase
}