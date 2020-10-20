package io.golos.use_cases.sign_up.core

import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.domain.fingerprint.FingerprintAuthManager
import io.golos.domain.repositories.AuthRepository
import io.golos.use_cases.auth.AuthUseCase
import io.golos.use_cases.sign_up.core.data_structs.SignUpCommand
import io.golos.use_cases.sign_up.core.data_structs.SingUpEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@FlowPreview
@ActivityScope
class SignUpCoreImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    stringsConverter: StringsConverter,
    @Named(Clarification.AES) private val encryptor: Encryptor,
    private val keyValueStorage: KeyValueStorageFacade,
    fingerprintAuthManager: FingerprintAuthManager,
    authUseCase: AuthUseCase,
    authRepository: AuthRepository,
    userKeyStore: UserKeyStore,
    signUpTokensRepository: SignUpTokensRepository
): SignUpCoreView, SignUpSMCoreTransition, SignUpCoreManagement, CoroutineScope {
    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext = scopeJob + dispatchersProvider.uiDispatcher
    
    private lateinit var innerSnapshot: SignUpSnapshotDomain

    private var currentState: SignUpState = SignUpState.SELECTING_SIGN_UP_METHOD

    private val transitionsFactory =
        SignUpCoreTransitionsFactory(
            dispatchersProvider,
            this,
            stringsConverter,
            encryptor,
            keyValueStorage,
            fingerprintAuthManager,
            authUseCase,
            authRepository,
            userKeyStore,
            signUpTokensRepository)

    private val commandsChannel = BroadcastChannel<SignUpCommand>(1)
    override val commands: Flow<SignUpCommand> = commandsChannel.asFlow()

    override fun init(snapshot: SignUpSnapshotDomain) {
        innerSnapshot = snapshot
        currentState = snapshot.state
    }

    override fun process(event: SingUpEvent) {
        launch {
            Timber.tag("SIGN_UP_TRANSITION").d("From: $currentState; on: ${event::class.simpleName}")

            val transitionResult = transitionsFactory.getTransition(currentState, event).process(event, innerSnapshot)

            currentState = transitionResult.newState

            withContext(dispatchersProvider.ioDispatcher) {
                if(transitionResult.clearSnapshot) {
                    keyValueStorage.removeSignUpSnapshot()
                } else {
                    innerSnapshot = (transitionResult.snapshot ?: innerSnapshot).copy(state = currentState)
                    keyValueStorage.saveSignUpSnapshot(innerSnapshot)
                }
            }
        }
    }

    override fun moveToStateDirect(newState: SignUpState) {
        currentState = newState
    }

    override fun getSnapshot(): SignUpSnapshotDomain = innerSnapshot.copy()

    override suspend fun sendCommand(command: SignUpCommand) = commandsChannel.send(command)

    override fun close() {
        scopeJob.cancel()
    }
}