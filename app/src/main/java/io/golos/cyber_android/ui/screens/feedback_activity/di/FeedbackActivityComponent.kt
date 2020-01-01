package io.golos.cyber_android.ui.screens.feedback_activity.di

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