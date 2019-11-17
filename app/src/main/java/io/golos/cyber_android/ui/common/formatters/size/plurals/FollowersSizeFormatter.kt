package io.golos.cyber_android.ui.common.formatters.size.plurals

import android.content.Context
import io.golos.cyber_android.R
import javax.inject.Inject

class FollowersSizeFormatter
@Inject
constructor(
    context: Context
) : PluralSizeFormatterBase(
    context,
    R.plurals.formatter_followers_formatted)

