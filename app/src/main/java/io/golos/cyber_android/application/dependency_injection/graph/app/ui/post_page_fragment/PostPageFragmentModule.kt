package io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment

import dagger.Module
import dagger.Provides
import io.golos.domain.use_cases.model.DiscussionIdModel

@Module
class PostPageFragmentModule(private val postId: DiscussionIdModel) {
    @Provides
    internal fun providePostId(): DiscussionIdModel = postId
}