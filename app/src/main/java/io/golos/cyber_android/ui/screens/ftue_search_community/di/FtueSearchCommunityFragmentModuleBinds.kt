package io.golos.cyber_android.ui.screens.ftue_search_community.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueSearchCommunityModel
import io.golos.cyber_android.ui.screens.ftue_search_community.model.FtueSearchCommunityModelImpl
import io.golos.cyber_android.ui.screens.ftue_search_community.view_model.FtueSearchCommunityViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface FtueSearchCommunityFragmentModuleBinds {

    @Binds
    @ViewModelKey(FtueSearchCommunityViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: FtueSearchCommunityViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @FragmentScope
    fun bindModel(model: FtueSearchCommunityModelImpl): FtueSearchCommunityModel
}