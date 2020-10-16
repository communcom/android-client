package io.golos.cyber_android.ui.screens.profile_black_list.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.profile_black_list.model.ProfileBlackListModel
import io.golos.cyber_android.ui.screens.profile_black_list.model.ProfileBlackListModelImpl
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.communities.ListWorkerCommunities
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.communities.ListWorkerCommunitiesImpl
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.users.ListWorkerUsers
import io.golos.cyber_android.ui.screens.profile_black_list.model.lists_workers.users.ListWorkerUsersImpl
import io.golos.cyber_android.ui.screens.profile_black_list.view_model.ProfileBlackListViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class ProfileBlackListFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(ProfileBlackListViewModel::class)
    abstract fun provideProfileBlackListViewModel(viewModel: ProfileBlackListViewModel): ViewModel

    @Binds
    abstract fun provideProfileBlackListModel(model: ProfileBlackListModelImpl): ProfileBlackListModel

    @Binds
    abstract fun provideListWorkerUsers(worker: ListWorkerUsersImpl): ListWorkerUsers

    @Binds
    abstract fun provideListWorkerCommunities(worker: ListWorkerCommunitiesImpl): ListWorkerCommunities
}