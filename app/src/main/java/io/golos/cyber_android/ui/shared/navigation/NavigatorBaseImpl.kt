package io.golos.cyber_android.ui.shared.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation

abstract class NavigatorBaseImpl(@IdRes private val navHostId: Int): NavigatorBase {
    /**
     * Move back in back stack
     */
    override fun moveBack(currentFragment: Fragment, finishCurrentActivity: Boolean) {
        if(finishCurrentActivity) {
            moveBack(currentFragment.activity!!)
        } else {
            getNavigationController(currentFragment).popBackStack()
        }
    }

    /**
     * Move back in back stack
     */
    override fun moveBack(currentActivity: FragmentActivity) = currentActivity.onBackPressed()

    protected fun getNavigationController(activity: FragmentActivity) = Navigation.findNavController(activity, navHostId)

    protected fun setHome(
        @IdRes fragmentId: Int,
        @NavigationRes navigationGraphId: Int,
        activity: FragmentActivity,
        destinationArgs: Bundle?) {

        val controller = getNavigationController(activity)
        val inflater = controller.navInflater
        val graph = inflater.inflate(navigationGraphId)
        graph.startDestination = fragmentId
        controller.setGraph(graph, destinationArgs)
    }

    private fun getNavigationController(currentFragment: Fragment) = getNavigationController(currentFragment.requireActivity())

}