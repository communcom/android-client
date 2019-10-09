package io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.main_activity.communities.select_community_dialog.SelectCommunityDialogViewModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModel
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.CommunityModelImpl
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.search.CommunitiesSearch
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.common.model.search.CommunitiesSearchImpl
import io.golos.domain.dependency_injection.scopes.FragmentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@Module
abstract class SelectCommunityDialogModuleBinds {
    @Binds
    @FragmentScope
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SelectCommunityDialogViewModel::class)
    abstract fun provideDiscoverViewModel(viewModel: SelectCommunityDialogViewModel): ViewModel

    @Binds
    abstract fun provideDiscoverModel(model: CommunityModelImpl): CommunityModel

    @Binds
    abstract fun provideSearchEngine(engine: CommunitiesSearchImpl): CommunitiesSearch
}