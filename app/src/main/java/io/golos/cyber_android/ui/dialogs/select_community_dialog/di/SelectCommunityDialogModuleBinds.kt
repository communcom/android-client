package io.golos.cyber_android.ui.dialogs.select_community_dialog.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.SelectCommunityDialogModel
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.SelectCommunityDialogModelImpl
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search.CommunitiesSearch
import io.golos.cyber_android.ui.dialogs.select_community_dialog.model.search.CommunitiesSearchImpl
import io.golos.cyber_android.ui.dialogs.select_community_dialog.view_model.SelectCommunityDialogViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
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
    abstract fun provideDiscoverModel(model: SelectCommunityDialogModelImpl): SelectCommunityDialogModel

    @Binds
    abstract fun provideSearchEngine(engine: CommunitiesSearchImpl): CommunitiesSearch
}