package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.leads_list_fragment

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.community_page_leaders_list.model.LeadsListModel
import io.golos.cyber_android.ui.screens.community_page_leaders_list.model.LeadsListModelImpl
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view_model.LeadsListViewModel

@Module
abstract class CommunityPageLeadsListModuleBinds {
    @Binds
    @ViewModelKey(LeadsListViewModel::class)
    @IntoMap
    abstract fun provideLeadsListViewModel(viewModel: LeadsListViewModel): ViewModel

    @Binds
    abstract fun provideLeadsListModel(model: LeadsListModelImpl): LeadsListModel
}