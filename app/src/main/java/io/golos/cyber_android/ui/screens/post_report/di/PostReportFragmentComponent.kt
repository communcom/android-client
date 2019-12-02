package io.golos.cyber_android.ui.screens.post_report.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.post_report.view.PostReportDialog
import io.golos.domain.dependency_injection.scopes.DialogScope

@Subcomponent(modules = [
    PostReportModule::class,
    PostReportFragmentModuleBinds::class
])
@DialogScope
interface PostReportFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: PostReportModule): Builder
        fun build(): PostReportFragmentComponent
    }

    fun inject(fragment: PostReportDialog)
}