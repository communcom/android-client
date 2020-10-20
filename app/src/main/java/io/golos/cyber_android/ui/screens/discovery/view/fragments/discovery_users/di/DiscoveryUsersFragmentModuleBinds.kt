package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.model.UserDiscoveryModel
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.model.UserDiscoveryModelImpl
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view_model.DiscoveryUserViewModel
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.ListWorkerFollowings
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.UsersListWorker
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class DiscoveryUsersFragmentModuleBinds {

    @Binds
    @IntoMap
    @ViewModelKey(DiscoveryUserViewModel::class)
    abstract fun providesDiscoveryViewModel(viewModel: DiscoveryUserViewModel): ViewModel

    @Binds
    abstract fun providesDiscoveryModel(userDiscoveryModel: UserDiscoveryModelImpl): UserDiscoveryModel

    @Binds
    abstract fun provideListWorkerFollowings(worker: ListWorkerFollowings): UsersListWorker

}
