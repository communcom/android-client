package io.golos.cyber_android.ui.dialogs.select_community_dialog.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.dialogs.select_community_dialog.view.SelectCommunityDialog
import io.golos.domain.dependency_injection.scopes.FragmentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@Subcomponent(modules = [SelectCommunityDialogModuleBinds::class])
@FragmentScope
interface SelectCommunityDialogComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SelectCommunityDialogComponent
    }

    fun inject(dialog: SelectCommunityDialog)
}