package io.golos.cyber_android.ui.di

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.screens.wallet_shared.balance_calculator.BalanceCalculator
import io.golos.cyber_android.ui.screens.wallet_shared.balance_calculator.BalanceCalculatorImpl
import io.golos.cyber_android.ui.shared.bitmaps.BitmapsUtilsImpl
import io.golos.cyber_android.ui.shared.camera.CameraHelper
import io.golos.cyber_android.ui.shared.camera.CameraHelperImpl
import io.golos.data.file_system.FileSystemHelperImpl
import io.golos.cyber_android.ui.shared.calculator.UICalculator
import io.golos.cyber_android.ui.shared.calculator.UICalculatorImpl
import io.golos.cyber_android.ui.shared.countries.CountriesRepository
import io.golos.cyber_android.ui.shared.countries.CountriesRepositoryImpl
import io.golos.cyber_android.ui.shared.helper.UIHelper
import io.golos.cyber_android.ui.shared.helper.UIHelperImpl
import io.golos.cyber_android.ui.shared.keyboard.KeyboardVisibilityListener
import io.golos.cyber_android.ui.shared.keyboard.KeyboardVisibilityListenerImpl
import io.golos.cyber_android.ui.shared.post_view.RecordPostViewManager
import io.golos.cyber_android.ui.shared.post_view.RecordPostViewManagerImpl
import io.golos.domain.BitmapsUtils
import io.golos.domain.FileSystemHelper
import io.golos.domain.dependency_injection.scopes.UIScope
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.images.ImageUploadUseCase
import io.golos.domain.use_cases.model.*
import io.golos.domain.use_cases.publish.EmbedsUseCase
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import io.golos.domain.mappers.new_mappers.CommentToModelMapperImpl

@Module
abstract class UIModuleBinds {
    @Binds
    abstract fun provideUICalculator(calculator: UICalculatorImpl): UICalculator

    @Binds
    abstract fun provideUIHelper(helper: UIHelperImpl): UIHelper

    @Binds
    abstract fun provideEmbedsUseCase(useCase: EmbedsUseCase): UseCase<ProccesedLinksModel>

    @Binds
    abstract fun getImageUploadUseCase(useCase: ImageUploadUseCase): UseCase<UploadedImagesModel>

    @UIScope
    @Binds
    abstract fun provideCommentToModelMapper(mapper: CommentToModelMapperImpl): CommentToModelMapper

    @Binds
    abstract fun provideBitmapUtils(utils: BitmapsUtilsImpl): BitmapsUtils

    @Binds
    abstract fun provideFileSystemHelper(helper: FileSystemHelperImpl): FileSystemHelper

    @Binds
    abstract fun provideCameraHelper(helper: CameraHelperImpl): CameraHelper

    @Binds
    @UIScope
    abstract fun provideCountriesRepository(repository: CountriesRepositoryImpl): CountriesRepository

    @Binds
    @UIScope
    abstract fun provideKeyboardVisibilityListener(listener: KeyboardVisibilityListenerImpl): KeyboardVisibilityListener

    @Binds
    abstract fun provideBalanceCalculator(calculator: BalanceCalculatorImpl): BalanceCalculator

    @Binds
    @UIScope
    abstract fun provideRecordPostViewManager(manager: RecordPostViewManagerImpl): RecordPostViewManager
}