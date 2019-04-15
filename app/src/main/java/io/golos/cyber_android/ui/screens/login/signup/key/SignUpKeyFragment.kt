package io.golos.cyber_android.ui.screens.login.signup.key

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.obsez.android.lib.filechooser.ChooserDialog
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.utils.PdfKeysUtils
import io.golos.domain.interactors.model.GeneratedUserKeys
import kotlinx.android.synthetic.main.fragment_sign_up_key.*


class SignUpKeyFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel

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
        viewModel.keysLiveData.observe(this, Observer { keys ->
            download.setOnClickListener {
                ChooserDialog(requireActivity())
                    .withFilter(true, false)
                    .displayPath(false)
                    .withChosenListener { path, _ ->
                        onSavePathSelected(path, keys)
                    }
                    .build()
                    .show()
            }
        })
    }

    private fun onSavePathSelected(path: String, keys: GeneratedUserKeys) {
        val saveResult = PdfKeysUtils.saveTextAsPdfDocument(PdfKeysUtils.getKeysSummary(requireContext(), keys), path)
        if (saveResult)
            onSaveSuccess()
        else onSaveError()
    }

    private fun onSaveError() {
        Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show()
    }

    private fun onSaveSuccess() {
        Toast.makeText(requireContext(), "Save success", Toast.LENGTH_SHORT).show()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }
}
