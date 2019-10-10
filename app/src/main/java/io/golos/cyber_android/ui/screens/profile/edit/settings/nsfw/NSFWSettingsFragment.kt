package io.golos.cyber_android.ui.screens.profile.edit.settings.nsfw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity.ProfileSettingsActivityComponent
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.profile.edit.settings.ProfileSettingsViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.data.errors.AppError
import io.golos.domain.entities.NSFWSettingsEntity
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_settings_picker.*
import javax.inject.Inject


class NSFWSettingsFragment : FragmentBase() {

    private lateinit var viewModel: ProfileSettingsViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.injections.get<ProfileSettingsActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings_picker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ivBack.setOnClickListener { findNavController().navigateUp() }
        settingsList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        settingsList.adapter = NSFWSettingsAdapter(object :
            NSFWSettingsAdapter.Listener {
            override fun onOptionClick(item: NSFWSettingsEntity) {
                viewModel.onNSFWOptionSelected(item)
                showLoading()
            }
        })
        setupViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getGeneralSettingsLiveData.observe(this, Observer {
            (settingsList.adapter as NSFWSettingsAdapter).selectedItem = it?.nsfws
        })

        viewModel.getReadinessLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { isReady ->
                if (isReady) {
                    hideLoading()
                } else {
                    showLoading()
                }
            }
        })

        viewModel.getUpdateState.asEvent().observe(this, Observer {
            it.getIfNotHandled()?.let { result ->
                when (result) {
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Success -> hideLoading()
                    is QueryResult.Error -> {
                        hideLoading()
                        val errorMsg = when (result.error) {
                            is AppError.RequestTimeOutException -> R.string.request_timeout_error
                            else -> R.string.unknown_error
                        }

                        NotificationDialog.newInstance(getString(errorMsg))
                            .show(requireFragmentManager(), "error")
                    }
                }
            }
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileSettingsViewModel::class.java)
    }
}
