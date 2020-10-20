package io.golos.domain.dependency_injection.scopes

import javax.inject.Scope

/**
 * Scope for global application objects
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
