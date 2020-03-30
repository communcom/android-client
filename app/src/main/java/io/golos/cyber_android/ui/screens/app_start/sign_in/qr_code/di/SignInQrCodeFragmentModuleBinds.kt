package io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactory
import io.golos.cyber_android.ui.shared.mvvm.viewModel.FragmentViewModelFactoryImpl
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelKey
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.model.SignInQrCodeModel
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.model.SignInQrCodeModelImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.detector.QrCodeDetector
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.detector.QrCodeDetectorImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.parser.QrCodeParser
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view.parser.QrCodeParserImpl
import io.golos.cyber_android.ui.screens.app_start.sign_in.qr_code.view_model.SignInQrCodeViewModel

@Module
abstract class SignInQrCodeFragmentModuleBinds {
    @Binds
    abstract fun provideViewModelFactory(factory: FragmentViewModelFactoryImpl): FragmentViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(SignInQrCodeViewModel::class)
    abstract fun provideViewModel(viewModel: SignInQrCodeViewModel): ViewModel

    @Binds
    abstract fun provideModel(model: SignInQrCodeModelImpl): SignInQrCodeModel

    @Binds
    abstract fun provideDetector(detector: QrCodeDetectorImpl): QrCodeDetector

    @Binds
    abstract fun provideParser(parser: QrCodeParserImpl): QrCodeParser
}