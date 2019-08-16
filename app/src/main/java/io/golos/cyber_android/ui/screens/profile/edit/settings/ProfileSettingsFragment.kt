package io.golos.cyber_android.ui.screens.profile.edit.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_settings_activity.ProfileSettingsActivityComponent
import io.golos.cyber_android.ui.common.extensions.observeUntil
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.keys_to_pdf.PdfKeysExporter
import io.golos.cyber_android.ui.common.keys_to_pdf.StartExportingCommand
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.dialogs.ConfirmationDialog
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.in_app_auth_activity.InAppAuthActivity
import io.golos.cyber_android.ui.screens.login_activity.LoginActivity
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.NotificationSetting
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.NotificationSettingsAdapter
import io.golos.data.errors.AppError
import io.golos.domain.entities.NSFWSettingsEntity
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.profile_settings_fragment.*
import java.text.MessageFormat
import javax.inject.Inject

class ProfileSettingsFragment : FragmentBase() {

    companion object {
        fun newInstance() = ProfileSettingsFragment()
    }

    private lateinit var viewModel: ProfileSettingsViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    private val keysExporter by lazy { PdfKeysExporter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.injections.get<ProfileSettingsActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.profile_settings_fragment, container, false)

    private var isExternalPushSettingsChange = false

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

        logOut.setOnClickListener {
            ConfirmationDialog.newInstance(getString(R.string.log_out_question)).apply {
                listener = ::logOut
            }.show(requireFragmentManager(), "logOut")
        }

        pushNotifsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isExternalPushSettingsChange) {
                viewModel.onPushSettingsSelected(isChecked)
            }
        }

        exportKeys.setOnClickListener {
            InAppAuthActivity.start(this)
        }

        keysExporter.setOnExportPathSelectedListener { viewModel.onExportPathSelected() }
        keysExporter.setOnExportErrorListener { uiHelper.showMessage(R.string.export_general_error) }

        versionText.text =
            MessageFormat.format(resources.getString(R.string.display_version), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        setupViewModel()
        observeViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            InAppAuthActivity.REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK) {
                    keysExporter.startExport()
                }
            }
            else -> keysExporter.processViewPdfResult(requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        keysExporter.processRequestPermissionsResult(requestCode, grantResults)
    }

    private fun logOut() {
        //on logger out we should disable push notifications
        viewModel.onPushSettingsSelected(false)

        //so we wait until we successfully disable it or until some error happens
        viewModel.getPushNotificationsSettingsLiveData.observeUntil(
            this,
            { it is QueryResult.Error ||
                    ((it as? QueryResult.Success)?.originalQuery?.isEnabled == false)
            }) {
            when (it) {
                is QueryResult.Success -> {
                    if (!it.originalQuery.isEnabled)
                        onLogoutSuccess()
                }
                is QueryResult.Loading -> {
                    showLoading()
                }
                is QueryResult.Error -> {
                    onLogoutError(it.error)
                }
            }
        }

    }

    private fun onLogoutSuccess() {
        hideLoading()
        viewModel.logOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    private fun onLogoutError(error: Throwable) {
        hideLoading()
        val errorMsg = when (error) {
            is AppError.RequestTimeOutException -> R.string.request_timeout_error
            else -> R.string.unknown_logout_error
        }
        NotificationDialog.newInstance(getString(errorMsg))
            .show(requireFragmentManager(), "error")
    }

    private fun observeViewModel() {
        viewModel.getNotificationSettingsLiveData.observe(this, Observer {
            (notificationSettingsList.adapter as NotificationSettingsAdapter).submit(it ?: emptyList())
        })

        viewModel.getPushNotificationsSettingsLiveData.observe(this, Observer {
            when (it) {
                is QueryResult.Success -> {
                    hideLoading()
                    isExternalPushSettingsChange = true
                    pushNotifsSwitch.isChecked = it.originalQuery.isEnabled
                    isExternalPushSettingsChange = false
                }
                is QueryResult.Loading -> {
                    showLoading()
                }
                is QueryResult.Error -> {
                    hideLoading()
                    val errorMsg = when (it.error) {
                        is AppError.RequestTimeOutException -> R.string.request_timeout_error
                        else -> R.string.unknown_error
                    }
                    NotificationDialog.newInstance(getString(errorMsg))
                        .show(requireFragmentManager(), "error")
                }
            }
        })

        viewModel.getGeneralSettingsLiveData.observe(this, Observer {
            language.text = it?.languageCode
            nsfw.text = when (it?.nsfws) {
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

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is StartExportingCommand -> keysExporter.processDataToExport(command.userName, command.userId, command.keys)
                is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)
                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
                else -> throw UnsupportedOperationException("This command is not supported")
            }
        })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileSettingsViewModel::class.java)
    }
}
