package io.golos.cyber_android.ui.screens.main_activity

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.ActivityMainBinding
import io.golos.cyber_android.ui.screens.main_activity.di.MainActivityComponent
import io.golos.cyber_android.ui.screens.main_activity.view_model.MainViewModel
import io.golos.cyber_android.ui.shared.mvvm.ActivityBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigationCommand
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
            val navigationController = mainNavHost.findNavController()
            navigationController.navigate(command)
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

    // note[AS] remove in a future
    fun showFragment(fragment: Fragment, isAddToBackStack: Boolean = true) {
        val tag = fragment::class.simpleName
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            if (isAddToBackStack) {
                beginTransaction.addToBackStack(tag)
            }

            beginTransaction.setCustomAnimations(
                R.anim.nav_slide_in_right,
                R.anim.nav_slide_out_left,
                R.anim.nav_slide_in_left,
                R.anim.nav_slide_out_right
            )

            beginTransaction
                .add(R.id.rootContainer, fragment, tag)
                .commit()
        }
    }
}
