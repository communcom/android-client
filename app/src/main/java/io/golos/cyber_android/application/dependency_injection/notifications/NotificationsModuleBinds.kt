package io.golos.cyber_android.application.dependency_injection.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.ViewModelKey
import io.golos.cyber_android.ui.screens.notifications.NotificationsViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class NotificationsModuleBinds {
    @Binds
    @FragmentScope
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    internal abstract fun provideCommunityFeedViewModel(viewModel: NotificationsViewModel): ViewModel
}