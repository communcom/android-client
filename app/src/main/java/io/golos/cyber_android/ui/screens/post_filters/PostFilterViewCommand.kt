package io.golos.cyber_android.ui.screens.post_filters

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand

class SendFilterActionCommand(
    val timeType: PostFiltersHolder.UpdateTimeFilter,
    val periodTime: PostFiltersHolder.PeriodTimeFilter
) : ViewCommand