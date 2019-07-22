package io.golos.cyber_android.application.dependency_injection.ui

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.ui.common.calculator.UICalculator
import io.golos.cyber_android.ui.common.calculator.UICalculatorImpl
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.helper.UIHelperImpl
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCaseImpl
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.interactors.reg.CountriesChooserUseCase
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.interactors.settings.SettingsUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.UserSettingModel
import io.golos.domain.requestmodel.VoteRequestModel

@Module
abstract class UIModuleBinds {
    @Binds
    abstract fun provideUICalculator(calculator: UICalculatorImpl): UICalculator

    @Binds
    abstract fun provideUIHelper(helper: UIHelperImpl): UIHelper

    @Binds
    abstract fun provideVoteUseCase(useCase: VoteUseCase): UseCase<MutableMap<DiscussionIdModel, QueryResult<VoteRequestModel>>>

    @Binds
    abstract fun getDiscussionPosterUseCase(useCase: DiscussionPosterUseCase): UseCase<QueryResult<DiscussionCreationResultModel>>

    @Binds
    abstract fun provideSignInUseCase(useCase: SignInUseCase): UseCase<UserAuthState>

    @Binds
    abstract fun provideSignOnUseCase(useCase: SignUpUseCase): UseCase<UserRegistrationStateModel>

    @Binds
    abstract fun provideEmbedsUseCase(useCase: EmbedsUseCase): UseCase<ProccesedLinksModel>

    @Binds
    abstract fun getCountriesChooserUseCase(useCase: CountriesChooserUseCase): UseCase<CountriesListModel>

    @Binds
    abstract fun getSettingUserCase(useCase: SettingsUseCase): UseCase<UserSettingModel>

    @Binds
    abstract fun getImageUploadUseCase(useCase: ImageUploadUseCase): UseCase<UploadedImagesModel>

    @Binds
    abstract fun getPushNotificationsSettingsUseCase(useCase: PushNotificationsSettingsUseCaseImpl): PushNotificationsSettingsUseCase
}