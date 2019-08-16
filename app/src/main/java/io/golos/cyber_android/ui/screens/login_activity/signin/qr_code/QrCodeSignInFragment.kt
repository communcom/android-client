package io.golos.cyber_android.ui.screens.login_activity.signin.qr_code

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.common.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.login_activity.signin.SignInArgs
import io.golos.cyber_android.ui.screens.login_activity.signin.SignInChildFragment
import io.golos.cyber_android.ui.screens.login_activity.signin.SignInParentFragment
import io.golos.cyber_android.ui.screens.login_activity.signin.SignInTab
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.detector.QrCodeDetector
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.detector.QrCodeDetectorErrorCode
import kotlinx.android.synthetic.main.fragment_qr_code_sign_in.*
import javax.inject.Inject

class QrCodeSignInFragment: FragmentBase(), SignInChildFragment {
    companion object {
        private const val REQUEST_CAMERA_PERMISSIONS = 3661

        fun newInstance(tab: SignInTab) =
            QrCodeSignInFragment()
                .apply {
                    arguments = Bundle().apply { putInt(SignInArgs.TAB_CODE, tab.index) }
                }
    }

    private lateinit var viewModel: QrCodeSignInViewModel

    private enum class VisibilityMode {
        INITIAL,
        NO_PERMISSIONS,
        DETECTION,
        INVALID_DETECTOR
    }

    private var detector: QrCodeDetector? = null

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    @Inject
    internal lateinit var appContext: Context

    override val tabCode: SignInTab by lazy {
        arguments!!.getInt(SignInArgs.TAB_CODE).let { SignInTab.fromIndex(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.injections.get<LoginActivityComponent>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_qr_code_sign_in, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        onSelected()
    }

    override fun onStop() {
        super.onStop()
        onUnselected()
    }

    override fun onSelected() {
        setVisibilityMode(VisibilityMode.INITIAL)

        uiHelper.setSoftKeyboardVisibility(root, false)

        if (!checkCameraPermissions()) {
            requestCameraPermissions()
        } else {
            initCodeReading()
        }
    }

    override fun onUnselected() {
        releaseCodeReading()
        uiHelper.hideMessage()
    }

    private fun checkCameraPermissions(): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermissions() = requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSIONS)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initCodeReading()
            } else {
                setVisibilityMode(VisibilityMode.NO_PERMISSIONS)
            }
        }
    }

    private fun initCodeReading() {
        setVisibilityMode(VisibilityMode.DETECTION)

        detector = QrCodeDetector(appContext)
            .apply {
                setOnCodeReceivedListener {
                    releaseCodeReading()
                    viewModel.onCodeReceived(it)
                }

                setOnDetectionErrorListener { errorCode ->
                    when(errorCode) {
                        QrCodeDetectorErrorCode.INVALID_CODE -> uiHelper.showMessage(R.string.sign_in_scan_qr_invalid_format)
                        QrCodeDetectorErrorCode.DETECTOR_IS_NOT_OPERATIONAL -> setVisibilityMode(VisibilityMode.INVALID_DETECTOR)
                    }
                }

                startDetection(requireContext(), cameraView)
            }
    }

    private fun releaseCodeReading() {
        detector?.stopDetection()
    }

    private fun setVisibilityMode(mode: VisibilityMode) {
        when(mode) {
            VisibilityMode.INITIAL -> {
                loadingIndicator.visibility = View.VISIBLE
                cameraGroup.visibility = View.GONE
                stubText.visibility = View.GONE
            }

            VisibilityMode.DETECTION -> {
                loadingIndicator.visibility = View.GONE
                cameraGroup.visibility = View.VISIBLE
                stubText.visibility = View.GONE
            }

            VisibilityMode.NO_PERMISSIONS -> {
                loadingIndicator.visibility = View.GONE
                cameraGroup.visibility = View.GONE
                stubText.visibility = View.VISIBLE

                stubText.text = requireContext().getText(R.string.sign_in_scan_qr_no_permissions)
            }

            VisibilityMode.INVALID_DETECTOR -> {
                loadingIndicator.visibility = View.GONE
                cameraGroup.visibility = View.GONE
                stubText.visibility = View.VISIBLE

                stubText.text = requireContext().getText(R.string.sign_in_scan_qr_detector_error)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(QrCodeSignInViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.loadingLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { isLoading ->
                setLoadingVisibility(isLoading)
            }
        })

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is ShowMessageCommand -> uiHelper.showMessage(command.textResId)
            }
        })

        viewModel.errorLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { isError ->
                if (isError)
                    showAuthErrorDialog()
            }
        })

        viewModel.authStateLiveData.observe(this, Observer {
            it.getIfNotHandled()?.let { state ->
                if (state.isUserLoggedIn) {
                    navigateForward()
                }
            }
        })
    }

    /**
     * Called when there was an error in login process, displays [NotificationDialog] with error message
     */
    private fun showAuthErrorDialog() {
        if (requireFragmentManager().findFragmentByTag("notification") == null)
            NotificationDialog
                .newInstance(getString(R.string.login_error))
                .setOnOkClickListener { initCodeReading()  }
                .show(requireFragmentManager(), "notification")
        hideLoading()
    }

    private fun navigateForward() {
        (requireParentFragment() as SignInParentFragment).navigateForward()
    }
}