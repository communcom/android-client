package io.golos.cyber_android.ui.screens.post_filters.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersDialog
import io.golos.domain.dependency_injection.scopes.DialogScope

@Subcomponent(modules = [PostFiltersFragmentModuleBinds::class, PostFilterFragmentModule::class])
@DialogScope
interface PostFiltersFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: PostFilterFragmentModule): Builder
        fun build(): PostFiltersFragmentComponent
    }

    fun inject(fragment: PostFiltersDialog)
}