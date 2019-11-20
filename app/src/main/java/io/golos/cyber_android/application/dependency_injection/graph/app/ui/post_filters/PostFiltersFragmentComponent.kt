package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_filters

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersDialog
import io.golos.domain.dependency_injection.scopes.DialogScope

@Subcomponent(modules = [PostFiltersFragmentModuleBinds::class])
@DialogScope
interface PostFiltersFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): PostFiltersFragmentComponent
    }

    fun inject(fragment: PostFiltersDialog)
}