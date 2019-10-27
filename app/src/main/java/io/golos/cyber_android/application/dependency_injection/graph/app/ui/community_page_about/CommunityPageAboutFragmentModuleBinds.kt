package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_about

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.community_page.CommunityPageModel
import io.golos.cyber_android.ui.screens.community_page.CommunityPageModelImpl
import io.golos.cyber_android.ui.screens.community_page.CommunityPageViewModel
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutModel
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutModelImpl
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.community.*

@Module
interface CommunityPageAboutFragmentModuleBinds {

    @Binds
    @ViewModelKey(CommunityPageAboutViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: CommunityPageAboutViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: CommunityPageAboutModelImpl): CommunityPageAboutModel
}