package io.golos.cyber_android.ui.screens.community_page_leaders_list.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.community_page_leaders_list.model.LeadsListModel
import io.golos.cyber_android.ui.screens.community_page_leaders_list.model.LeadsListModelImpl
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view_model.LeadsListViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class CommunityPageLeadsListModuleBinds {
    @Binds
    @ViewModelKey(LeadsListViewModel::class)
    @IntoMap
    abstract fun provideLeadsListViewModel(viewModel: LeadsListViewModel): ViewModel

    @Binds
    abstract fun provideLeadsListModel(model: LeadsListModelImpl): LeadsListModel
}