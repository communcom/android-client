package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.main_activity.communities.model.CommunitiesModel
import io.golos.cyber_android.ui.screens.main_activity.communities.model.CommunitiesModelImpl
import io.golos.cyber_android.ui.screens.main_activity.communities.view_model.CommunitiesViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class CommunitiesFragmentModuleBinds {
    @Binds
    @FragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(CommunitiesViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: CommunitiesViewModel): ViewModel

    @Binds
    abstract fun provideDiscoverModel(model: CommunitiesModelImpl): CommunitiesModel
}