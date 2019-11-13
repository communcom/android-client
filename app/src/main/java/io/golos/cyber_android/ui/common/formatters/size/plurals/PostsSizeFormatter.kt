package io.golos.cyber_android.ui.common.formatters.size.plurals

import io.golos.cyber_android.R
import io.golos.domain.AppResourcesProvider
import javax.inject.Inject

class PostsSizeFormatter
@Inject
constructor(
    appResources: AppResourcesProvider
) : PluralSizeFormatterBase(
    appResources,
    R.plurals.formatter_posts_formatted)
