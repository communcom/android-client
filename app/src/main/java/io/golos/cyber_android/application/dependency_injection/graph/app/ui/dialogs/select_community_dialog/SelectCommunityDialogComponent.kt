package io.golos.cyber_android.application.dependency_injection.graph.app.ui.dialogs.select_community_dialog

import dagger.Subcomponent
import io.golos.cyber_android.ui.dialogs.select_community_dialog.view.SelectCommunityDialog
import io.golos.domain.dependency_injection.scopes.FragmentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@Subcomponent(modules = [
    SelectCommunityDialogModule::class,
    SelectCommunityDialogModuleBinds::class
])
@FragmentScope
interface SelectCommunityDialogComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SelectCommunityDialogComponent
    }

    fun inject(dialog: SelectCommunityDialog)
}