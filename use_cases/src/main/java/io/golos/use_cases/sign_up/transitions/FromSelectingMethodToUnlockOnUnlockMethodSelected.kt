package io.golos.use_cases.sign_up.transitions

import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.dto.FtueBoardStageEntity
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.sign_up.core.SignUpSMCoreTransition
import io.golos.use_cases.sign_up.core.data_structs.*
import io.golos.use_cases.sign_up.dto.TransitionResult
import kotlinx.coroutines.withContext
import timber.log.Timber

class FromSelectingMethodToUnlockOnUnlockMethodSelected(
    private val dispatchersProvider: DispatchersProvider,
    parent: SignUpSMCoreTransition,
    private val keyValueStorage: KeyValueStorageFacade,
    private val authUseCase: AuthUseCase
) : SingUpTransitionBase<UnlockMethodSelected>(parent, SignUpState.ENTERING_PIN) {
    /**
     * Process event and return new state of the SM
     */
    override suspend fun process(event: UnlockMethodSelected, snapshot: SignUpSnapshotDomain): TransitionResult {
        // Auth
        try {
            parent.sendCommand(ShowLoading())
            authUseCase.auth(snapshot.userName!!, snapshot.password!!)
            parent.sendCommand(HideLoading())
        } catch (ex: Exception) {
            Timber.e(ex)
            parent.sendCommand(HideLoading())
            parent.sendCommand(ShowError(SignUpMessageCode.AUTH_ERROR))
            return getResult()
        }

        // Settings
        try {
            withContext(dispatchersProvider.ioDispatcher) {
                keyValueStorage.saveAppUnlockWay(event.unlockMethod)
                keyValueStorage.saveFtueBoardStage(FtueBoardStageEntity.NEED_SHOW)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            parent.sendCommand(ShowError(SignUpMessageCode.GENERAL_ERROR))
            parent.sendCommand(NavigateToSelectMethod())
            return getResult(SignUpState.SELECTING_SIGN_UP_METHOD, clearSnapshot = true)
        }

        parent.sendCommand(SingUpCompleted())
        return getResult(SignUpState.FINAL,  clearSnapshot = true)
    }
}