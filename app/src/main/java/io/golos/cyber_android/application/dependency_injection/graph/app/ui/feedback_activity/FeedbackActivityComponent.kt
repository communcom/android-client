package io.golos.cyber_android.application.dependency_injection.graph.app.ui.feedback_activity

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.feedback_activity.FeedbackActivity
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Subcomponent
@ActivityScope
interface FeedbackActivityComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): FeedbackActivityComponent
    }

    fun inject(activity: FeedbackActivity)
}