package io.golos.cyber_android.ui.screens.login_sign_up.fragments.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenViewModelBase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpVerificationViewModel
@Inject
constructor(
    private val analyticsFacade: AnalyticsFacade
): SignUpScreenViewModelBase() {

    companion object {
        private const val RESEND_TIMEOUT_SECONDS = 59
    }

    private val secondsUntilResendLiveData = MutableLiveData<Int>(RESEND_TIMEOUT_SECONDS)

    val getSecondsUntilResendLiveData = secondsUntilResendLiveData as LiveData<Int>

    private var lastTimerJob: Job? = null

    init {
        analyticsFacade.openScreen113()
        restartResendTimer()
    }

    fun restartResendTimer() {
        lastTimerJob?.cancel()
        lastTimerJob = viewModelScope.launch {
            for (tick in RESEND_TIMEOUT_SECONDS downTo 0) {
                secondsUntilResendLiveData.postValue(tick)
                delay(1_000)
            }
        }
    }

    override fun validate(field: String): Boolean {
        return field.length == 4
    }
}