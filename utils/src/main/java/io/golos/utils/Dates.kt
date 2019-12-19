package io.golos.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.toServerFormat(): String = SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US).format(this)

private const val DATE_SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.ssZ"