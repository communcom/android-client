package io.golos.cyber_android.ui.shared.utils

import android.os.Bundle
import androidx.navigation.NavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.dashboard.view.DashboardFragment
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationToDashboardCommand

fun NavController.navigate(navigationCommand: NavigationCommand){
    val args = if(navigationCommand is NavigationToDashboardCommand && navigationCommand.intent != null) {
        Bundle().apply { putParcelable(DashboardFragment.INTENT_KEY, navigationCommand.intent) }
    } else {
        null
    }

    navigationCommand.graphId?.let {
        val inflater = this.navInflater
        val graph = inflater.inflate(R.navigation.graph_main)
        navigationCommand.startDestination?.let {
            graph.startDestination = navigationCommand.startDestination
        }
        this.setGraph(graph, args)
    }
    navigationCommand.startDestination?.let {
        this.graph.startDestination = navigationCommand.startDestination
    }
    navigationCommand.navigationId?.let {
        this.navigate(it, args)
    }
}