package io.golos.use_cases.sign_up.core

import io.golos.domain.dto.sign_up.SignUpSnapshotDomain
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.core.data_structs.SignUpCommand
import io.golos.use_cases.sign_up.core.data_structs.SingUpEvent
import kotlinx.coroutines.flow.Flow

interface SignUpCoreView {
    val commands: Flow<SignUpCommand>

    fun process(event: SingUpEvent)

    fun moveToStateDirect(newState: SignUpState)

    fun getSnapshot(): SignUpSnapshotDomain
}

interface SignUpSMCoreTransition {
    suspend fun sendCommand(command: SignUpCommand)
}

interface SignUpCoreManagement {
    fun init(snapshot: SignUpSnapshotDomain)

    fun close()
}