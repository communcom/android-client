package io.golos.cyber_android.ui.screens.login_sign_up.fragments.keys_backup

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
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
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.common.extensions.safeNavigate
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.keys_to_pdf.PdfKeysExporter
import io.golos.cyber_android.ui.common.keys_to_pdf.StartExportingCommand
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpViewModel
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.keys_backup.view_commands.NavigateToOnboardingCommand
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.onboardingImage.OnboardingUserImageFragment
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_sign_up_protection_keys.*
import javax.inject.Inject


class SignUpProtectionKeysFragment : FragmentBase() {
    @Parcelize
    data class Args(
        val userCyberName: String
    ): Parcelable

    private lateinit var viewModel: SignUpProtectionKeysViewModel
    private lateinit var signUpViewModel: SignUpViewModel

    private val keysExporter by lazy { PdfKeysExporter(this) }

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.injections.get<LoginActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up_protection_keys, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        keysList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        backup.setOnClickListener { keysExporter.startExport() }
        keysExporter.setOnExportCompletedListener { viewModel.onBackupCompleted() }
        keysExporter.setOnExportPathSelectedListener { viewModel.onExportPathSelected() }
        keysExporter.setOnExportErrorListener { uiHelper.showMessage(R.string.export_general_error) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        keysExporter.processViewPdfResult(requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        keysExporter.processRequestPermissionsResult(requestCode, grantResults)
    }

    private fun navigateToOnboarding(user: UserIdDomain) {
        findNavController().safeNavigate(
            R.id.signUpProtectionKeysFragment,
            R.id.action_signUpProtectionKeysFragment_to_onboardingUserImageFragment,
            Bundle().apply { putParcelable(Tags.ARGS, OnboardingUserImageFragment.Args(user.userId) )}
        )
    }

    private fun observeViewModel() {
        viewModel.keysLiveData.observe(this, Observer {
            keysList.adapter = KeysAdapter(it)
        })

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is NavigateToOnboardingCommand -> navigateToOnboarding(command.user)
                is StartExportingCommand -> keysExporter.processDataToExport(command.userName, command.userId, command.keys)
                is SetLoadingVisibilityCommand -> setLoadingVisibility(command.isVisible)
                is ShowMessageResCommand -> uiHelper.showMessage(command.textResId)
                else -> throw UnsupportedOperationException("This command is not supported")
            }
       })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpProtectionKeysViewModel::class.java)

        signUpViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(SignUpViewModel::class.java)
    }
}
