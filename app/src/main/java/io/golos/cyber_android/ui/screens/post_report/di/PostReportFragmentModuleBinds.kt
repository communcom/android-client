package io.golos.cyber_android.ui.screens.post_report.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.post_report.model.PostReportModel
import io.golos.cyber_android.ui.screens.post_report.model.PostReportModelImpl
import io.golos.cyber_android.ui.screens.post_report.view_model.PostReportViewModel
import io.golos.domain.dependency_injection.scopes.DialogScope

@Module
interface PostReportFragmentModuleBinds {

    @Binds
    @ViewModelKey(PostReportViewModel::class)
    @IntoMap
    fun bindViewModel(viewModel: PostReportViewModel): ViewModel

    @Binds
    @DialogScope
    fun bindViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    fun bindModel(model: PostReportModelImpl): PostReportModel
}