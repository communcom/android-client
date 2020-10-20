package io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.CommunityAllProposalsViewModel
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.model.CommunityAllProposalsModel
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.model.CommunityAllProposalsModelImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class CommunityAllProposalsModuleBinds {

    @Binds
    @IntoMap
    @ViewModelKey(CommunityAllProposalsViewModel::class)
    abstract fun bindProposalViewModel(viewModel: CommunityAllProposalsViewModel): ViewModel

    @Binds
    abstract fun bindProposalModel(model: CommunityAllProposalsModelImpl): CommunityAllProposalsModel

}