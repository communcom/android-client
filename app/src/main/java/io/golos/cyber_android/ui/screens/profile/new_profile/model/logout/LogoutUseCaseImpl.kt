package io.golos.cyber_android.ui.screens.profile.new_profile.model.logout

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import io.golos.cyber_android.ui.screens.login_activity.LoginActivity
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.use_cases.user.UsersRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

class LogoutUseCaseImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade,
    private val usersRepository: UsersRepository,
    private val appContext: Context
) : LogoutUseCase {
    override suspend fun logout() {
        withContext(dispatchersProvider.ioDispatcher) {
            userKeyStore.clearAllKeys()

            keyValueStorage.removeAuthState()
            keyValueStorage.removePinCode()
            keyValueStorage.removeAppUnlockWay()
            keyValueStorage.removeLastUsedCommunityId()
            usersRepository.clearCurrentUserData()
        }
    }

    override fun restartApp() {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val startActivity = Intent(appContext, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val pendingIntentId = 403904954
            val pendingIntent = PendingIntent.getActivity(
                appContext,
                pendingIntentId,
                startActivity,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            val mgr = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
        }
        exitProcess(0)
    }
}