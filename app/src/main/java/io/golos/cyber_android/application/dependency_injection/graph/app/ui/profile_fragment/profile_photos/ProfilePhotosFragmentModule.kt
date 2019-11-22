package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_photos

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.PhotoPlace
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class ProfilePhotosFragmentModule(private val place: PhotoPlace, private val imageUrl: String?) {
    @Provides
    fun providePlace(): PhotoPlace = place

    @Provides
    @Named(Clarification.IMAGE_URL)
    fun provideImageUrl(): String? = imageUrl
}