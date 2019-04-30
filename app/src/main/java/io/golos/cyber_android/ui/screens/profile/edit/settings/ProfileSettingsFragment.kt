package io.golos.cyber_android.ui.screens.profile.edit.settings

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
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.NotificationSetting
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.NotificationSettingsAdapter
import io.golos.domain.entities.NSFWSettingsEntity
import kotlinx.android.synthetic.main.profile_settings_fragment.*

class ProfileSettingsFragment : LoadingFragment() {

    companion object {
        fun newInstance() = ProfileSettingsFragment()
    }

    private lateinit var viewModel: ProfileSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_settings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        notificationSettingsList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        notificationSettingsList.adapter =
            NotificationSettingsAdapter(object :
                NotificationSettingsAdapter.Listener {
                override fun onSettingsChanged(
                    item: NotificationSetting,
                    isEnabled: Boolean
                ) {
                    viewModel.onNotificationSettingChanged(item, isEnabled)
                }

            })

        nsfwButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileSettingsFragment_to_NSFWSettingsFragment)
        }

        languageButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileSettingsFragment_to_LanguageSettingsFragment)
        }

        back.setOnClickListener { requireActivity().finish() }

        setupViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getNotificationSettingsLiveData.observe(this, Observer {
            (notificationSettingsList.adapter as NotificationSettingsAdapter).submit(it)
        })

        viewModel.getGeneralSettingsLiveData.observe(this, Observer {
            language.text = it.languageCode
            nsfw.text = when(it.nsfws) {
                NSFWSettingsEntity.ALERT_WARN -> getString(R.string.always_alert)
                NSFWSettingsEntity.ALWAYS_HIDE -> getString(R.string.always_hide)
                NSFWSettingsEntity.ALWAYS_SHOW -> getString(R.string.always_show)
                else -> getString(R.string.missing_setting_descr)
            }
        })

        viewModel.getReadinessLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { isReady ->
                if (isReady) {
                    hideLoading()
                    settings.visibility = View.VISIBLE
                } else {
                    showLoading()
                    settings.visibility = View.GONE
                }
            }
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getProfileSettingsViewModelFactory()
        ).get(ProfileSettingsViewModel::class.java)
    }

}
