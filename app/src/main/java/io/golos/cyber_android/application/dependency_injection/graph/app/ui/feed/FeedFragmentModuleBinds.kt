package io.golos.cyber_android.application.dependency_injection.graph.app.ui.feed

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.feed.FeedModel
import io.golos.cyber_android.ui.screens.feed.FeedModelImpl
import io.golos.cyber_android.ui.screens.feed.FeedViewModel
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
    @FragmentScope
    fun bindModel(model: FeedModelImpl): FeedModel
}