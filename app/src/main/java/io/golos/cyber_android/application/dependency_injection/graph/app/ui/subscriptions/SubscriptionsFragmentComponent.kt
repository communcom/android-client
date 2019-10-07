package io.golos.cyber_android.application.dependency_injection.graph.app.ui.subscriptions

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.subscriptions.SubscriptionsFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [SubscriptionsFragmentModuleBinds::class])
@FragmentScope
interface SubscriptionsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): SubscriptionsFragmentComponent
    }

    fun inject(fragment: SubscriptionsFragment)
}