package io.golos.cyber_android.application.dependency_injection.graph.app.ui

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.core.bitmaps.BitmapsUtilsImpl
import io.golos.cyber_android.core.file_system.FileSystemHelperImpl
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacade
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacadeImpl
import io.golos.cyber_android.ui.common.calculator.UICalculator
import io.golos.cyber_android.ui.common.calculator.UICalculatorImpl
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.formatters.size.plurals.FollowersSizeFormatter
import io.golos.cyber_android.ui.common.formatters.size.plurals.PostsSizeFormatter
import io.golos.cyber_android.ui.common.helper.UIHelper
import io.golos.cyber_android.ui.common.helper.UIHelperImpl
import io.golos.domain.BitmapsUtils
import io.golos.domain.FileSystemHelper
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.UIScope
import io.golos.domain.use_cases.UseCase
import io.golos.domain.use_cases.action.VoteUseCase
import io.golos.domain.use_cases.images.ImageUploadUseCase
import io.golos.domain.use_cases.model.*
import io.golos.domain.use_cases.notifs.events.EventsUseCase
import io.golos.domain.use_cases.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.use_cases.notifs.push.PushNotificationsSettingsUseCaseImpl
import io.golos.domain.use_cases.publish.DiscussionPosterUseCase
import io.golos.domain.use_cases.publish.EmbedsUseCase
import io.golos.domain.use_cases.reg.SignUpUseCase
import io.golos.domain.use_cases.settings.SettingsUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.mappers.EventEntityToModelMapper
import io.golos.domain.mappers.EventEntityToModelMapperImpl
import io.golos.domain.mappers.new_mappers.CommentToModelMapper
import io.golos.domain.mappers.new_mappers.CommentToModelMapperImpl
import io.golos.domain.requestmodel.EventsListModel
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.requestmodel.UserSettingModel
import io.golos.domain.requestmodel.VoteRequestModel
import javax.inject.Named

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
    abstract fun provideEventEntityToModelMapper(mapper: EventEntityToModelMapperImpl): EventEntityToModelMapper

    @UIScope
    @Binds
    abstract fun provideCommentToModelMapper(mapper: CommentToModelMapperImpl): CommentToModelMapper

    @Binds
    abstract fun provideEventsUseCase(useCase: EventsUseCase): UseCase<EventsListModel>

    @Binds
    abstract fun provideBitmapUtils(utils: BitmapsUtilsImpl): BitmapsUtils

    @Binds
    abstract fun provideFileSystemHelper(helper: FileSystemHelperImpl): FileSystemHelper

    //region Formatters
    @Binds
    @Named(Clarification.FOLLOWERS)
    abstract fun provideFollowersSizeFormatter(formatter: FollowersSizeFormatter): SizeFormatter

    @Binds
    @Named(Clarification.POSTS)
    abstract fun providePostsSizeFormatter(formatter: PostsSizeFormatter): SizeFormatter
    //endregion
}