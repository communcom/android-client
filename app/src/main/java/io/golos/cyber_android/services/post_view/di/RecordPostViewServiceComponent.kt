package io.golos.cyber_android.services.post_view.di

import dagger.Subcomponent
import io.golos.cyber_android.services.post_view.RecordPostViewService
import io.golos.domain.dependency_injection.scopes.ServiceScope

@Subcomponent()
@ServiceScope
interface RecordPostViewServiceComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): RecordPostViewServiceComponent
    }

    fun inject(service: RecordPostViewService)
}