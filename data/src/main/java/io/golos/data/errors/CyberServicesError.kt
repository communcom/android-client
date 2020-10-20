package io.golos.data.errors

import io.golos.commun4j.sharedmodel.Either

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
data class CyberServicesError(val error: Either.Failure<out Any, out Any>) : IllegalArgumentException(error.value.toString())