package io.golos.cyber_android.ui.screens.community_page.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.community_page.model.CommunityPageModel
import io.golos.cyber_android.ui.screens.community_page.model.CommunityPageModelImpl
import io.golos.cyber_android.ui.screens.community_page.view_model.CommunityPageViewModel
import io.golos.domain.use_cases.community.*

@Module
interface CommunityPageFragmentModuleBinds {
    @Binds
    @ViewModelKey(CommunityPageViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: CommunityPageViewModel): ViewModel

    @Binds
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