package io.golos.utils.helpers

import java.util.*

fun Date.add(milliseconds: Long): Date = Date(time + milliseconds)