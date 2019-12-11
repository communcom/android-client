package io.golos.cyber_android.ui.utils

import androidx.navigation.NavController
import io.golos.cyber_android.ui.common.mvvm.view_commands.NavigationCommand

fun NavController.navigate(navigationCommand: NavigationCommand){
    navigationCommand.startDestination?.let {
        this.graph.startDestination = it
    }
    navigationCommand.navigationId?.let {
        this.navigate(it)
    } ?: this.popBackStack()
}