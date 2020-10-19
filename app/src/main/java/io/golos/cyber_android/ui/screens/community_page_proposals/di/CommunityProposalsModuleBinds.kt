package io.golos.cyber_android.ui.screens.community_page_proposals.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.community_page_proposals.model.CommunityProposalsModel
import io.golos.cyber_android.ui.screens.community_page_proposals.model.CommunityProposalsModelImpl
import io.golos.cyber_android.ui.screens.community_page_proposals.view_model.CommunityProposalsViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class CommunityProposalsModuleBinds {

    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(CommunityProposalsViewModel::class)
    abstract fun bindViewModel(viewModel: CommunityProposalsViewModel): ViewModel

    @Binds
    abstract fun bindModel(model: CommunityProposalsModelImpl): CommunityProposalsModel
}