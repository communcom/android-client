package io.golos.domain.interactors.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.Repository
import io.golos.domain.entities.AuthState
import io.golos.domain.entities.GeneralSettingEntity
import io.golos.domain.entities.NotificationSettingsEntity
import io.golos.domain.entities.UserSettingEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.requestmodel.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
class SettingsUseCase(
    private val settingsRepository: Repository<UserSettingEntity, SettingChangeRequest>,
    private val authRepository: Repository<AuthState, AuthRequest>
) :
    UseCase<UserSettingModel> {

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()
    private val userSettings = MutableLiveData<UserSettingModel>()
    private val readinessLiveData = MutableLiveData<Boolean>()

    val getSettingsReadiness: LiveData<Boolean> = readinessLiveData

    override val getAsLiveData: LiveData<UserSettingModel>
        get() = userSettings

    private val updateState = MutableLiveData<QueryResult<*>>()

    val getUpdateState: LiveData<QueryResult<*>> = updateState

    override fun subscribe() {
        super.subscribe()
        mediator.addSource(authRepository.getAsLiveData(authRepository.allDataRequest)) {
            readinessLiveData.value = it?.isUserLoggedIn == true
        }
        mediator.addSource(settingsRepository.updateStates) {
            val lastRequest = this.lastRequest ?: return@addSource
            it[lastRequest.id]?.let { result ->
                updateState.value = result
            }
        }
        mediator.addSource(settingsRepository.getAsLiveData(SettingsFetchRequest())) {
            it ?: return@addSource
            val notifSettings = it.notifsSettings
            userSettings.value = UserSettingModel(
                GeneralSettingsModel(it.general.nsfws, it.general.languageCode),
                NotificationSettingsModel(
                    notifSettings.showUpvote,
                    notifSettings.showDownvote,
                    notifSettings.showReply,
                    notifSettings.showTransfer,
                    notifSettings.showSubscribe,
                    notifSettings.showUnsubscribe,
                    notifSettings.showMention,
                    notifSettings.showRepost,
                    notifSettings.showMessage,
                    notifSettings.showWitnessVote,
                    notifSettings.showWitnessCancelVote,
                    notifSettings.showReward,
                    notifSettings.showCuratorReward
                )
            )
        }
        mediator.observeForever(observer)
        makeAction(SettingsFetchRequestModel())
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(authRepository.getAsLiveData(authRepository.allDataRequest))
        mediator.removeSource(settingsRepository.getAsLiveData(SettingsFetchRequest()))
    }

    private var lastRequest: SettingChangeRequest? = null

    fun makeAction(param: SettingChangeRequestModel) {
        if (readinessLiveData.value != true) {
            println("cannot make settings actions, if user not authenticated")
            return
        }
        val repositoryParams = when (param) {
            is ChangeBasicSettingsRequestModel -> ChangeBasicSettingsRequest(
                GeneralSettingEntity(
                    param.newGeneralSettings.nsfws,
                    param.newGeneralSettings.languageCode
                )
            )
            is ChangeNotificationSettingRequestModel -> ChangeNotificationSettingRequest(
                NotificationSettingsEntity(
                    param.newNotificationSettings.showUpvote,
                    param.newNotificationSettings.showDownvote,
                    param.newNotificationSettings.showReply,
                    param.newNotificationSettings.showTransfer,
                    param.newNotificationSettings.showSubscribe,
                    param.newNotificationSettings.showUnsubscribe,
                    param.newNotificationSettings.showMention,
                    param.newNotificationSettings.showRepost,
                    param.newNotificationSettings.showMessage,
                    param.newNotificationSettings.showWitnessVote,
                    param.newNotificationSettings.showWitnessCancelVote,
                    param.newNotificationSettings.showReward,
                    param.newNotificationSettings.showCuratorReward
                )
            )
            is SettingsFetchRequestModel -> SettingsFetchRequest()
        }
        lastRequest = repositoryParams
        settingsRepository.makeAction(
            repositoryParams
        )
    }
}