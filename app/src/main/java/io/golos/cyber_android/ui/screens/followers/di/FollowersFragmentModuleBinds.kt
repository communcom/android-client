package io.golos.cyber_android.ui.screens.followers.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.followers.Follower
import io.golos.cyber_android.ui.screens.followers.FollowersModel
import io.golos.cyber_android.ui.screens.followers.FollowersModelImpl
import io.golos.cyber_android.ui.screens.followers.FollowersViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.use_cases.user.*

@Module
interface FollowersFragmentModuleBinds {

    @Binds
    @ViewModelKey(FollowersViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: FollowersViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: FollowersModelImpl): FollowersModel

    @Binds
    fun bindPaginator(impl: Paginator.Store<Follower>): Paginator.Store<Follower>

    @Binds
    fun bindGetFollowersUseCase(useCase: GetFollowersUseCaseImpl): GetFollowersUseCase

    @Binds
    fun bindSubscribeToFollowerUseCase(useCase: SubscribeToFollowerUseCaseImpl): SubscribeToFollowerUseCase

    @Binds
    fun bindUnsubscribeToFollowerUseCase(useCase: UnsubscribeToFollowerUseCaseImpl): UnsubscribeToFollowerUseCase
}