package io.golos.cyber_android.ui.screens.login.signup.keys

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.obsez.android.lib.filechooser.ChooserDialog
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.ui.screens.login.signup.onboarding.image.OnboardingUserImageFragment
import io.golos.cyber_android.utils.PdfKeysUtils
import io.golos.domain.UserKeyStore
import io.golos.sharedmodel.CyberName
import kotlinx.android.synthetic.main.fragment_sign_up_protection_keys.*
import java.io.File

const val PERMISSION_REQUEST = 100
const val VIEW_PDF_REQUEST = 101

class SignUpProtectionKeysFragment : Fragment() {

    data class Args(val user: CyberName)

    private lateinit var viewModel: SignUpProtectionKeysViewModel
    private lateinit var signUpViewModel: SignUpViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_protection_keys, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        keysList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        backup.setOnClickListener {
            requireContext().serviceLocator.backupManager.dataChanged()
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIEW_PDF_REQUEST) {
            viewModel.backupCompleted()
        }
    }

    private fun showSaveDialog() {
        signUpViewModel.lastRegisteredUser.value?.let {
            ChooserDialog(requireActivity())
                .withFilter(true, false)
                .displayPath(false)
                .withChosenListener { path, _ ->
                    onSavePathSelected(path, it, signUpViewModel.userKeyStore)
                }
                .build()
                .show()
        }
    }

    private fun onSavePathSelected(path: String, userName: String, userKeyStore: UserKeyStore) {
        val saveResult = PdfKeysUtils.saveTextAsPdfDocument(PdfKeysUtils.getKeysSummary(requireContext(), userName, userKeyStore), path)
        if (saveResult)
            onSaveSuccess(PdfKeysUtils.getKeysSavePathInDir(path))
        else onSaveError()
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
                viewModel.backupCompleted()
            }
        }

        dialog.isCancelable = false
        dialog.show(requireFragmentManager(), "notification")
    }


    private fun onSaveError() {
        Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToOnboarding(user: CyberName) {
        findNavController().safeNavigate(
            R.id.signUpProtectionKeysFragment,
            R.id.action_signUpProtectionKeysFragment_to_onboardingUserImageFragment,
            Bundle().apply {
                putString(
                    Tags.ARGS,
                    requireContext()
                        .serviceLocator.moshi
                        .adapter(OnboardingUserImageFragment.Args::class.java)
                        .toJson(OnboardingUserImageFragment.Args(user))
                )
            }
        )
    }

    private fun observeViewModel() {
        viewModel.keysLiveData.observe(this, Observer {
            keysList.adapter = KeysAdapter(it)
        })

        viewModel.command.observe(this, Observer { command ->
            when(command) {
                is NavigateToOnboardingCommand -> navigateToOnboarding(command.user)
                else -> throw UnsupportedOperationException("This command is not supported")
            }
       })
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireContext().serviceLocator.getDefaultViewModelFactory()
        ).get(SignUpProtectionKeysViewModel::class.java)

        signUpViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getDefaultViewModelFactory()
        ).get(SignUpViewModel::class.java)

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
}
