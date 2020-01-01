package io.golos.cyber_android.ui.screens.profile_photos.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.profile_photos.model.ProfilePhotosModel
import io.golos.cyber_android.ui.screens.profile_photos.model.ProfilePhotosModelImpl
import io.golos.cyber_android.ui.screens.profile_photos.model.gallery_items_source.GalleryItemsSource
import io.golos.cyber_android.ui.screens.profile_photos.model.gallery_items_source.GalleryItemsSourceImpl
import io.golos.cyber_android.ui.screens.profile_photos.model.result_bitmap_calculator.ResultBitmapCalculator
import io.golos.cyber_android.ui.screens.profile_photos.model.result_bitmap_calculator.ResultBitmapCalculatorImpl
import io.golos.cyber_android.ui.screens.profile_photos.view_model.ProfilePhotosViewModel

@Module
abstract class ProfilePhotosFragmentModuleBinds {
    @Binds
    @IntoMap
    @ViewModelKey(ProfilePhotosViewModel::class)
    abstract fun provideProfilePhotosViewModel(viewModel: ProfilePhotosViewModel): ViewModel

    @Binds
    abstract fun provideProfilePhotosModel(model: ProfilePhotosModelImpl): ProfilePhotosModel

    @Binds
    abstract fun provideGalleryItemsSource(source: GalleryItemsSourceImpl): GalleryItemsSource

    @Binds
    abstract fun provideResultBitmapCalculator(calculator: ResultBitmapCalculatorImpl): ResultBitmapCalculator
}