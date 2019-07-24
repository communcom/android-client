package io.golos.cyber_android.application.dependency_injection.user_posts_feed

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    UserPostsFeedModule::class,
    UserPostsFeedModuleBinds::class
])
@FragmentScope
interface UserPostsFeedComponent {
}