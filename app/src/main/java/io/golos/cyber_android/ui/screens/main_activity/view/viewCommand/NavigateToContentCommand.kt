package io.golos.cyber_android.ui.screens.main_activity.view.viewCommand

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand

class NavigateToContentCommand(val contentPage: ContentPage) : ViewCommand

enum class ContentPage{
    DASHBOARD,
    FTUE
}