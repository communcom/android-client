package io.golos.cyber_android.ui.shared.extensions

import android.content.Context
import io.golos.cyber_android.R
import io.golos.domain.repositories.exceptions.ApiResponseErrorException

fun Throwable.getMessage(context: Context): String =
    when(this) {
        is ApiResponseErrorException -> this.errorInfo.message ?: context.getString(R.string.common_general_error)
        else ->  context.getString(R.string.common_general_error)
    }
