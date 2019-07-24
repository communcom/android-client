package io.golos.cyber_android.application.dependency_injection.my_feed

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    MyFeedModule::class,
    MyFeedModuleBinds::class
])
@FragmentScope
interface MyFeedComponent {
}