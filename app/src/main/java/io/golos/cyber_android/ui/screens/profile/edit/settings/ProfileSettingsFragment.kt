package io.golos.cyber_android.ui.screens.profile.edit.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.profile_settings_fragment.*

class ProfileSettingsFragment : Fragment() {

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
        notificationSettingsList.adapter = NotificationSettingsAdapter(object : NotificationSettingsAdapter.Listener {
            override fun onSettingChanged(item: NotificationSetting, isEnabled: Boolean) {
                viewModel.onNotificationSettingChanged(item, isEnabled)
            }

        })

        setupViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getSettingsLiveData.observe(this, Observer {
            (notificationSettingsList.adapter as NotificationSettingsAdapter).submit(it)
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileSettingsViewModel::class.java)
    }

}
