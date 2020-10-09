package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.model.CommunityCommentReportsModel
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.model.CommunityCommentReportsModelImpl
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.view_model.CommunityCommentReportsViewModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey

@Module
abstract class  CommunityCommentReportsModuleBinds {

    @Binds
    @IntoMap
    @ViewModelKey(CommunityCommentReportsViewModel::class)
    abstract fun allReportsBindViewModel(viewModel: CommunityCommentReportsViewModel): ViewModel

    @Binds
    abstract fun allReportsBindModel(model: CommunityCommentReportsModelImpl): CommunityCommentReportsModel
}