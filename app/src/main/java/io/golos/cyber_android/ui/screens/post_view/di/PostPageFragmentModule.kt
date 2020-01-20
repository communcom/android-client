package io.golos.cyber_android.ui.screens.post_view.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.use_cases.model.DiscussionIdModel

@Module
class PostPageFragmentModule(
    private val postId: DiscussionIdModel,
    private val postContentId: ContentId
) {
    @Provides
    internal fun providePostId(): DiscussionIdModel = postId

    @Provides
    internal fun providePostContentId(): ContentId = postContentId

}