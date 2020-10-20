package io.golos.cyber_android.application.di_storage

import io.golos.commun4j.Commun4j
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.data.ServerMessageReceiver
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import javax.inject.Inject

/**
 * Wrapper around [Cyber4J]
 */
@ApplicationScope
open class Cyber4JDagger
@Inject
constructor(config: Commun4jConfig, serverMessageReceiver: ServerMessageReceiver) : Commun4j(
    config = config,
    serverMessageCallback = serverMessageReceiver
)