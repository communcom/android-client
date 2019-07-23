package io.golos.cyber_android.application.dependency_injection.post_page

import dagger.Module
import dagger.Provides
import io.golos.domain.interactors.model.DiscussionIdModel

@Module
class PostPageModule(private val postId: DiscussionIdModel) {
    @Provides
    internal fun providePostId(): DiscussionIdModel = postId
}