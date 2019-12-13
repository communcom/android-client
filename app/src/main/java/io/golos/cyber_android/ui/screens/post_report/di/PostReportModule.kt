package io.golos.cyber_android.ui.screens.post_report.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post

@Module
class PostReportModule(private val contentId: ContentId) {

    @Provides
    internal fun provideContentId(): ContentId = contentId

}