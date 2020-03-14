package io.golos.cyber_android.ui.screens.main_activity

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.ActivityMainBinding
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.main_activity.view_model.MainViewModel
import io.golos.cyber_android.ui.shared.mvvm.ActivityBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationToDashboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.utils.navigate
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : ActivityBaseMVVM<ActivityMainBinding, MainViewModel>() {

    override fun provideViewModelType(): Class<MainViewModel> = MainViewModel::class.java

    override fun layoutResId(): Int = R.layout.activity_main

    override fun inject(key: String) = App.injections.get<MainActivityComponent>(key).inject(this)

    override fun releaseInjection(key: String) {
        App.injections.release<MainActivityComponent>(key)
    }

    override fun linkViewModel(binding: ActivityMainBinding, viewModel: MainViewModel) {
        binding.viewModel = viewModel
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        if (command is NavigationCommand) {
            processNavigation(command)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun processNavigation(command: NavigationCommand) {
        mainNavHost.findNavController()
            .let { navigationController ->
                if(command is NavigationToDashboardCommand && intent.action == Intent.ACTION_VIEW) {
                    NavigationToDashboardCommand(
                        navigationId = command.navigationId,
                        startDestination = command.startDestination,
                        graphId = command.graphId,
                        deepLinkUri = intent.data
                    )
                } else {
                    command
                }
                .let { navigationController.navigate(it) }
            }
    }
}
