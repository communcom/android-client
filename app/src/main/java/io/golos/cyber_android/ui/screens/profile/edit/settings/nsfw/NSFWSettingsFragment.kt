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
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.screens.profile.edit.settings.ProfileSettingsViewModel
import io.golos.domain.entities.NSFWSettingsEntity
import kotlinx.android.synthetic.main.fragment_settings_picker.*


class NSFWSettingsFragment : LoadingFragment() {

    private lateinit var viewModel: ProfileSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_picker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        back.setOnClickListener { findNavController().navigateUp() }
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
            hideLoading()
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
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getDefaultViewModelFactory()
        ).get(ProfileSettingsViewModel::class.java)
    }
}
