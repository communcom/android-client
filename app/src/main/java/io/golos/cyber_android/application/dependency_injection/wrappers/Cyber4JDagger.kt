package io.golos.cyber_android.application.dependency_injection.wrappers

import io.golos.cyber4j.Cyber4J
import io.golos.cyber4j.sharedmodel.Cyber4JConfig
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import javax.inject.Inject

/**
 * Wrapper around [Cyber4J]
 */
@ApplicationScope
class Cyber4JDagger
@Inject
constructor(config: Cyber4JConfig): Cyber4J(config)