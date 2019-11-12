package io.golos.cyber_android.ui.screens.main_activity.communities.dto

import io.golos.cyber_android.ui.common.recycler_view.ListItem

class PageLoadResult(
    val hasNextData: Boolean,
    val data: List<ListItem>?
)