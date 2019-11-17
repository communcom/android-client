package io.golos.cyber_android.ui.common.formatters.size.plurals

import android.content.Context
import io.golos.cyber_android.R
import javax.inject.Inject

class PostsSizeFormatter
@Inject
constructor(
    context: Context
) : PluralSizeFormatterBase(
    context,
    R.plurals.formatter_posts_formatted)
