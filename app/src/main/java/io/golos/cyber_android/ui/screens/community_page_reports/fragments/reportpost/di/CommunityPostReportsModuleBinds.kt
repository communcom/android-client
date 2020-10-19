package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.model.CommunityPostReportsModel
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.model.CommunityPostReportsModelImpl
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view_model.CommunityPostReportsViewModel

@Module
abstract class  CommunityPostReportsModuleBinds {

    @Binds
    @IntoMap
    @ViewModelKey(CommunityPostReportsViewModel::class)
    abstract fun allReportsBindViewModel(viewModel: CommunityPostReportsViewModel): ViewModel

    @Binds
    abstract fun allReportsBindModel(model: CommunityPostReportsModelImpl): CommunityPostReportsModel
}