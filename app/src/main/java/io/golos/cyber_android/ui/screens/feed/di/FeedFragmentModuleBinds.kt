package io.golos.cyber_android.ui.screens.feed.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.feed.FeedViewModel
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersModel
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersModelImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface FeedFragmentModuleBinds {

    @Binds
    @ViewModelKey(FeedViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: FeedViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: PostFiltersModelImpl): PostFiltersModel
}