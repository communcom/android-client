package io.golos.cyber_android.ui.screens.profile_comments.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModel
import io.golos.cyber_android.ui.screens.profile_comments.model.ProfileCommentsModelImpl
import io.golos.cyber_android.ui.screens.profile_comments.view_model.ProfileCommentsViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Module
interface ProfileCommentsFragmentModuleBinds {

    @Binds
    @ViewModelKey(ProfileCommentsViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: ProfileCommentsViewModel): ViewModel

    @Binds
    @FragmentScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: ProfileCommentsModelImpl): ProfileCommentsModel
}