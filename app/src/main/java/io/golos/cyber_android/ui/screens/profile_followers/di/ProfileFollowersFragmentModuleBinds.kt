package io.golos.cyber_android.ui.screens.profile_followers.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile_followers.model.ProfileFollowersModel
import io.golos.cyber_android.ui.screens.profile_followers.model.ProfileFollowersModelImpl
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.UsersListWorker
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.ListWorkerFollowers
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.ListWorkerFollowings
import io.golos.cyber_android.ui.screens.profile_followers.model.lists_workers.ListWorkerMutual
import io.golos.cyber_android.ui.screens.profile_followers.view_model.ProfileFollowersViewModel
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
abstract class ProfileFollowersFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(ProfileFollowersViewModel::class)
    abstract fun provideProfileFollowersViewModel(viewModel: ProfileFollowersViewModel): ViewModel

    @Binds
    abstract fun provideProfileFollowersModel(model: ProfileFollowersModelImpl): ProfileFollowersModel

    @Binds
    @Named(Clarification.FOLLOWERS)
    abstract fun provideListWorkerFollowers(worker: ListWorkerFollowers): UsersListWorker

    @Binds
    @Named(Clarification.FOLLOWING)
    abstract fun provideListWorkerFollowings(worker: ListWorkerFollowings): UsersListWorker

    @Binds
    @Named(Clarification.MUTUAL)
    abstract fun provideListWorkerMutual(worker: ListWorkerMutual): UsersListWorker
}