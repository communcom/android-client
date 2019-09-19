package io.golos.cyber_android.application.dependency_injection.graph.app.ui

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.core.bitmaps.BitmapsUtilsImpl
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacade
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacadeImpl
import io.golos.cyber_android.ui.common.calculator.UICalculator
import io.golos.cyber_android.ui.common.calculator.UICalculatorImpl
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.helper.UIHelperImpl
import io.golos.domain.BitmapsUtils
import io.golos.domain.dependency_injection.scopes.UIScope
import io.golos.domain.entities.EventsListEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.action.VoteUseCase
import io.golos.domain.interactors.images.ImageUploadUseCase
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.notifs.events.EventsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.interactors.notifs.push.PushNotificationsSettingsUseCaseImpl
import io.golos.domain.interactors.publish.DiscussionPosterUseCase
import io.golos.domain.interactors.publish.EmbedsUseCase
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.interactors.settings.SettingsUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.requestmodel.EventsListModel
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.UserSettingModel
import io.golos.domain.requestmodel.VoteRequestModel
import io.golos.domain.rules.EntityToModelMapper
import io.golos.domain.rules.EventEntityToModelMapper

@Module
abstract class UIModuleBinds {
    @Binds
    abstract fun provideUICalculator(calculator: UICalculatorImpl): UICalculator

    @Binds
    abstract fun provideUIHelper(helper: UIHelperImpl): UIHelper

    @Binds
    abstract fun provideVoteUseCase(useCase: VoteUseCase): UseCase<MutableMap<DiscussionIdModel, QueryResult<VoteRequestModel>>>

    @Binds
    abstract fun provideDiscussionPosterUseCase(useCase: DiscussionPosterUseCase): UseCase<QueryResult<DiscussionCreationResultModel>>

    @Binds
    abstract fun provideSignInUseCase(useCase: SignInUseCase): UseCase<UserAuthState>

    @Binds
    abstract fun provideSignOnUseCase(useCase: SignUpUseCase): UseCase<UserRegistrationStateModel>

    @Binds
    abstract fun provideEmbedsUseCase(useCase: EmbedsUseCase): UseCase<ProccesedLinksModel>

    @Binds
    abstract fun getSettingUserCase(useCase: SettingsUseCase): UseCase<UserSettingModel>

    @Binds
    abstract fun getImageUploadUseCase(useCase: ImageUploadUseCase): UseCase<UploadedImagesModel>

    @Binds
    abstract fun getPushNotificationsSettingsUseCase(useCase: PushNotificationsSettingsUseCaseImpl): PushNotificationsSettingsUseCase

    @Binds
    abstract fun provideBackupKeysFacadeSync(facade: BackupKeysFacadeImpl): BackupKeysFacade

    @UIScope
    @Binds
    abstract fun provideEventEntityToModelMapper(mapper: EventEntityToModelMapper): EntityToModelMapper<EventsListEntity, EventsListModel>

    @Binds
    abstract fun provideEventsUseCase(useCase: EventsUseCase): UseCase<EventsListModel>

    @Binds
    abstract fun provideBitmapUtils(utils: BitmapsUtilsImpl): BitmapsUtils
}