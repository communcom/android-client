package io.golos.cyber_android.application.dependency_injection.graph.app.ui.subscriptions

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.paginator.Paginator
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsModel
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsModelImpl
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.interactors.community.*
import javax.inject.Named
import javax.inject.Qualifier

@Module
interface SubscriptionsFragmentModuleBinds {

    @Binds
    @ViewModelKey(SubscriptionsViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: SubscriptionsViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: SubscriptionsModelImpl): SubscriptionsModel

    @Binds
    @Named("PaginatorSubscriptions")
    fun bindPaginatorSubscriptions(impl: Paginator.Store<Community>): Paginator.Store<Community>

    @Binds
    @Named("PaginatorRecomendedCommunities")
    fun bindPaginatorRecomendedCommunities(impl: Paginator.Store<Community>): Paginator.Store<Community>

    @Binds
    fun bindCheckSubscriptionsOnCommunitiesUseCase(model: CheckSubscriptionsOnCommunityUseCaseImpl): CheckSubscriptionsOnCommunitiesUseCase

    @Binds
    fun bindGetCommunitiesUseCase(model: GetCommunitiesUseCaseImpl): GetCommunitiesUseCase

    @Binds
    fun bindGetRecommendedCommunitiesUseCase(model: GetRecommendedCommunitiesUseCaseImpl): GetRecommendedCommunitiesUseCase
}