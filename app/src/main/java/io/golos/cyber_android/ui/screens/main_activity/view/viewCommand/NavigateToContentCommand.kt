package io.golos.cyber_android.ui.screens.main_activity.view.viewCommand

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand

class NavigateToContentCommand(val contentPage: ContentPage) : ViewCommand

enum class ContentPage {
    DASHBOARD {
        override val navigationId: Int
            get() = R.id.dashboardFragment
    },
    FTUE {
        override val navigationId: Int
            get() = R.id.ftueFragment
    };

    abstract val navigationId: Int
}