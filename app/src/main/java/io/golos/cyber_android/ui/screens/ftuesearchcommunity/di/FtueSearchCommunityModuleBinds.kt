package io.golos.cyber_android.ui.screens.ftuesearchcommunity.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.model.FtueSearchCommunityModel
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.model.FtueSearchCommunityModelImpl
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.viewmodel.FtueSearchCommunityViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface FtueSearchCommunityModuleBinds {

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