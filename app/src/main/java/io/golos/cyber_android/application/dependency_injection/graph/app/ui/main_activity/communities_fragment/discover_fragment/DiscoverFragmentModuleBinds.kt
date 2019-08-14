package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.formatters.size.FollowersSizeFormatter
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.viewModel.DiscoverViewModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.model.DiscoverModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.model.DiscoverModelImpl
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Module
abstract class DiscoverFragmentModuleBinds {
    @Binds
    @SubFragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(DiscoverViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: DiscoverViewModel): ViewModel

    @Binds
    abstract fun provideDiscoverModel(model: DiscoverModelImpl): DiscoverModel

    @Binds
    @SubFragmentScope
    abstract fun provideFollowersSizeFormatter(formatter: FollowersSizeFormatter): SizeFormatter
}