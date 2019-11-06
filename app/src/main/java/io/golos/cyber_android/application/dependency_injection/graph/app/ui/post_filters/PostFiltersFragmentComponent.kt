package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_filters

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersBottomSheetDialog
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [PostFiltersFragmentModuleBinds::class])
@FragmentScope
interface PostFiltersFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): PostFiltersFragmentComponent
    }

    fun inject(fragment: PostFiltersBottomSheetDialog)
}