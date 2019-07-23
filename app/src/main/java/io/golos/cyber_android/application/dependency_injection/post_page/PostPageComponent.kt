package io.golos.cyber_android.application.dependency_injection.post_page

import dagger.Subcomponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    PostPageModule::class,
    PostPageModuleBinds::class
])
@FragmentScope
interface PostPageComponent {
}