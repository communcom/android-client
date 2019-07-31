package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.notifications_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.main_activity.notifications.NotificationsViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class NotificationsFragmentModuleBinds {
    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    internal abstract fun provideCommunityFeedViewModel(viewModel: NotificationsViewModel): ViewModel
}