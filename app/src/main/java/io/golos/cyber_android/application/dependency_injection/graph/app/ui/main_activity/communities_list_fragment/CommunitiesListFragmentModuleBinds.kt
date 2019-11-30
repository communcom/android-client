package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_list_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModel
import io.golos.cyber_android.ui.screens.communities_list.model.CommunitiesListModelImpl
import io.golos.cyber_android.ui.screens.communities_list.view_model.CommunitiesListViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class CommunitiesListFragmentModuleBinds {
    @Binds
    @FragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(CommunitiesListViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: CommunitiesListViewModel): ViewModel

    @Binds
    abstract fun provideDiscoverModel(model: CommunitiesListModelImpl): CommunitiesListModel
}