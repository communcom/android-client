package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page

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
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.community.*

@Module
interface CommunityPageFragmentModuleBinds {

    @Binds
    @ViewModelKey(CommunityPageViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: CommunityPageViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: CommunityPageModelImpl): CommunityPageModel

    @Binds
    fun bindGetCommunityPageByIdUseCase(getCommunityPageByIdUseCase: GetCommunityPageByIdUseCaseImpl): GetCommunityPageByIdUseCase

    @Binds
    fun bindSubscribeToCommunityUseCase(model: SubscribeToCommunityUseCaseImpl): SubscribeToCommunityUseCase

    @Binds
    fun bindUnsubscribeToCommunityUseCase(model: UnsubscribeToCommunityUseCaseImpl): UnsubscribeToCommunityUseCase
}