package io.golos.cyber_android.ui.screens.profile_black_list.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_black_list.view.ProfileBlackListFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [ProfileBlackListFragmentModuleBinds::class, ProfileBlackListFragmentModule::class])
@SubFragmentScope
interface ProfileBlackListFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileBlackListFragmentModule): Builder
        fun build(): ProfileBlackListFragmentComponent
    }

    fun inject(fragment: ProfileBlackListFragment)
}