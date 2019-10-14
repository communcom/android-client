package io.golos.cyber_android.application.dependency_injection.graph.app.ui.followers

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.paginator.Paginator
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.followers.Follower
import io.golos.cyber_android.ui.screens.followers.FollowersModel
import io.golos.cyber_android.ui.screens.followers.FollowersModelImpl
import io.golos.cyber_android.ui.screens.followers.FollowersViewModel
import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsModel
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsModelImpl
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.community.*
import javax.inject.Named

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
}