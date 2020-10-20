package io.golos.cyber_android.ui.screens.post_report.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.ContentIdDomain

@Module
class PostReportModule(private val contentId: ContentIdDomain) {

    @Provides
    internal fun provideContentId(): ContentIdDomain = contentId

}