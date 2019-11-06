package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_filters

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersModel
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersModelImpl
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersViewModel
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsViewModel
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface PostFiltersFragmentModuleBinds {

    @Binds
    @ViewModelKey(SubscriptionsViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: PostFiltersViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: PostFiltersModelImpl): PostFiltersModel
}