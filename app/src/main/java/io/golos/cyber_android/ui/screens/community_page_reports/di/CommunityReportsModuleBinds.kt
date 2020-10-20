package io.golos.cyber_android.ui.screens.community_page_reports.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.community_page_reports.model.CommunityReportsModel
import io.golos.cyber_android.ui.screens.community_page_reports.model.CommunityReportsModelImpl
import io.golos.cyber_android.ui.screens.community_page_reports.view_model.CommunityReportsViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
abstract class CommunityReportsModuleBinds {

    @Binds
    abstract fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(CommunityReportsViewModel::class)
    abstract fun bindViewModel(viewModel: CommunityReportsViewModel): ViewModel

    @Binds
    abstract fun bindModel(model: CommunityReportsModelImpl): CommunityReportsModel
}