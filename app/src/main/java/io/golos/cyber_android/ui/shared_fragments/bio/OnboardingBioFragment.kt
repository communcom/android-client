package io.golos.cyber_android.ui.shared_fragments.bio

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.ui.dialogs.NotificationDialog
import io.golos.cyber_android.ui.screens.main_activity.MainActivity
import io.golos.cyber_android.utils.asEvent
import io.golos.cyber_android.views.utils.TextWatcherBase
import io.golos.data.errors.AppError
import io.golos.domain.requestmodel.QueryResult
import io.golos.sharedmodel.CyberName
import kotlinx.android.synthetic.main.fragment_onboarding_bio.*

class OnboardingBioFragment : FragmentBase() {

    data class Args(val user: CyberName)

    companion object {
        fun newInstance() = OnboardingBioFragment()
    }

    private lateinit var viewModel: EditProfileBioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_bio, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()
        bio.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onBioChanged(s.toString())
            }
        })
        bio.filters = arrayOf(InputFilter.LengthFilter(EditProfileBioViewModel.MAX_BIO_LENGTH))

        next.setOnClickListener { viewModel.updateBio(false) }
        skip.setOnClickListener {
            goToMainScreen()
        }
    }

    private fun goToMainScreen() {
        if (!requireActivity().isFinishing) {
            requireActivity().finish()
            startActivity(Intent(requireContext(), MainActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.getMetadataUpdateStateLiveData.asEvent().observe(this, Observer { event ->
            event?.getIfNotHandled()?.let {
                when (it) {
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Error -> onError(it.error)
                    is QueryResult.Success -> onSuccess()
                }
            }
        })
    }

    private fun onSuccess() {
        hideLoading()
        goToMainScreen()
    }

    private fun onError(error: Throwable) {
        hideLoading()
        val errorMsg = when (error) {
            is AppError.RequestTimeOutException -> R.string.request_timeout_error
            else -> R.string.unknown_error
        }

        NotificationDialog.newInstance(getString(errorMsg))
            .show(requireFragmentManager(), "error")
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getViewModelFactoryByCyberName(getArgs().user)
        ).get(EditProfileBioViewModel::class.java)
    }

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!
}
