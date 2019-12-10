package io.golos.cyber_android.ui.screens.ftue.view.view_command

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand

class NavigateToFtuePageCommand(val page: FtuePage): ViewCommand

enum class FtuePage{

    SEARCH_COMMUNITIES {

        override fun getPagePosition(): Int = 0
    },

    FINISH {

        override fun getPagePosition(): Int = 1
    };

    abstract fun getPagePosition(): Int
}