package io.golos.cyber_android.ui.screens.login.signup.key

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.obsez.android.lib.filechooser.ChooserDialog
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.login.AuthViewModel
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.ui.screens.main.MainActivity
import io.golos.cyber_android.utils.PdfKeysUtils
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.interactors.model.GeneratedUserKeys
import io.golos.domain.requestmodel.SignInState
import kotlinx.android.synthetic.main.fragment_sign_up_key.*
import java.io.File


const val PERMISSION_REQUEST = 100
const val VIEW_PDF_REQUEST = 101

class SignUpKeyFragment : Fragment() {

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var authViewModel: AuthViewModel

    private var fileSavedSuccessful = false
    private var authSuccessful = false
    private var keys: GeneratedUserKeys? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_key, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        signUpViewModel.keysLiveData.observe(this, Observer { keys ->
            this.keys = keys
            download.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST)
                } else
                    showSaveDialog()
            }
        })

        authViewModel.authLiveData.asEvent().observe(this, Observer { event ->
            event.getIfNotHandled()?.let {
                authSuccessful = it == SignInState.USER_LOGGED_IN
                tryToNavigateToMainScreen()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                showSaveDialog()
            else onSaveError()
            return
        }
    }

    private fun showSaveDialog() {
        keys?.let {
            ChooserDialog(requireActivity())
                .withFilter(true, false)
                .displayPath(false)
                .withChosenListener { path, _ ->
                    onSavePathSelected(path, it)
                }
                .build()
                .show()
        }
    }

    private fun onSavePathSelected(path: String, keys: GeneratedUserKeys) {
        val saveResult = PdfKeysUtils.saveTextAsPdfDocument(PdfKeysUtils.getKeysSummary(requireContext(), keys), path)
        if (saveResult)
            onSaveSuccess(PdfKeysUtils.getKeysSavePathInDir(path))
        else onSaveError()
    }

    private fun onSaveError() {
        Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show()
    }

    private fun onSaveSuccess(keysSavePath: String) {
        val dialog = NotificationDialog
            .newInstance(resources.getString(R.string.keys_saved_successfully))

        dialog.listener = {
            val intent = Intent(Intent.ACTION_VIEW)
            val mimeType = "application/pdf"
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                val fileURI = FileProvider.getUriForFile(
                    context!!,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    File(keysSavePath)
                )
                intent.setDataAndType(fileURI, mimeType)
            } else {
                intent.setDataAndType(Uri.fromFile(File(keysSavePath)), mimeType)
            }
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                startActivityForResult(intent, VIEW_PDF_REQUEST)
            } catch (e: ActivityNotFoundException) {
                fileSavedSuccessful = true
                tryToNavigateToMainScreen()
            }
        }

        dialog.isCancelable = false
        dialog.show(requireFragmentManager(), "notification")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIEW_PDF_REQUEST) {
            fileSavedSuccessful = true
            tryToNavigateToMainScreen()
        }
    }

    private fun tryToNavigateToMainScreen() {
        if (authSuccessful && fileSavedSuccessful)
            navigateToMainScreen()
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun setupViewModel() {
        signUpViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)

        authViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getAuthViewModelFactory()
        ).get(AuthViewModel::class.java)
    }
}
