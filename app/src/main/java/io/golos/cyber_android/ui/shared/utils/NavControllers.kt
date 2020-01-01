package io.golos.cyber_android.ui.shared.utils

import androidx.navigation.NavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationCommand

fun NavController.navigate(navigationCommand: NavigationCommand){
    navigationCommand.graphId?.let {
        val inflater = this.navInflater
        val graph = inflater.inflate(R.navigation.graph_main)
        navigationCommand.startDestination?.let {
            graph.startDestination = navigationCommand.startDestination
        }
        this.graph = graph
    }
    navigationCommand.startDestination?.let {
        this.graph.startDestination = navigationCommand.startDestination
    }
    navigationCommand.navigationId?.let {
        this.navigate(it)
    }
}