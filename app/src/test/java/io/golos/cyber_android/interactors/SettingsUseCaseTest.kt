package io.golos.cyber_android.interactors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.golos.cyber_android.authStateRepository
import io.golos.cyber_android.settingsRepo
import io.golos.domain.entities.NSFWSettingsEntity
import io.golos.domain.entities.NotificationSettingsEntity
import io.golos.domain.interactors.settings.SettingsUseCase
import io.golos.domain.requestmodel.ChangeBasicSettingsRequestModel
import io.golos.domain.requestmodel.ChangeNotificationSettingRequestModel
import io.golos.domain.requestmodel.GeneralSettingsModel
import io.golos.domain.requestmodel.SettingsFetchRequestModel
import junit.framework.Assert.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
class SettingsUseCaseTest {
    @Rule
    @JvmField
    public val rule = InstantTaskExecutorRule()

    lateinit var case: SettingsUseCase

    @Before
    fun before() {
        case = SettingsUseCase(settingsRepo, authStateRepository)
    }

    @Test
    fun testSettings() = runBlocking {
        case.subscribe()
        case.unsubscribe()
        case.subscribe()

        var settings = case.getAsLiveData.value
        case.getAsLiveData.observeForever {
            settings = it
        }
        case.getSettingsReadiness.observeForever { }

        while (case.getSettingsReadiness.value != true) delay(100)

        case.makeAction(SettingsFetchRequestModel())

        assertNotNull(settings)

        case.makeAction(
            ChangeNotificationSettingRequestModel(
                NotificationSettingsEntity(
                    false, false, false
                    , false, false, false, false, false,
                    false, false,
                    false, false, false
                )
            )
        )
        assertTrue(!settings!!.notifsSettings.showCuratorReward)
        assertTrue(!settings!!.notifsSettings.showMessage)
        assertTrue(!settings!!.notifsSettings.showUpvote)
        assertTrue(!settings!!.notifsSettings.showDownvote)
        assertTrue(!settings!!.notifsSettings.showMention)
        assertTrue(!settings!!.notifsSettings.showReply)
        assertTrue(!settings!!.notifsSettings.showRepost)
        assertTrue(!settings!!.notifsSettings.showReward)
        assertTrue(!settings!!.notifsSettings.showSubscribe)
        assertTrue(!settings!!.notifsSettings.showUnsubscribe)
        assertTrue(!settings!!.notifsSettings.showTransfer)
        assertTrue(!settings!!.notifsSettings.showWitnessCancelVote)
        assertTrue(!settings!!.notifsSettings.showWitnessVote)

        case.makeAction(ChangeBasicSettingsRequestModel(GeneralSettingsModel(NSFWSettingsEntity.ALERT_WARN, "ru")))

        assertEquals("ru",settings!!.general.languageCode)
        assertEquals(NSFWSettingsEntity.ALERT_WARN, settings!!.general.nsfws)


    }
}