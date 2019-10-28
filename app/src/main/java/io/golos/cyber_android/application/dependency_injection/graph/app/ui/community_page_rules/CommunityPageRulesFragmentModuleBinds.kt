package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_rules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesModel
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesModelImpl
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface CommunityPageRulesFragmentModuleBinds {

    @Binds
    @ViewModelKey(CommunityPageRulesViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: CommunityPageRulesViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: CommunityPageRulesModelImpl): CommunityPageRulesModel
}