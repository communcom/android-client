package io.golos.cyber_android.ui.screens.post_report.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.Post

@Module
class PostReportModule(private val contentId: Post.ContentId) {

    @Provides
    internal fun provideContentId(): Post.ContentId = contentId

}