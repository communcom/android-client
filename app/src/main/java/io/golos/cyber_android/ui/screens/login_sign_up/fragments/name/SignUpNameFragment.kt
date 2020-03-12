package io.golos.cyber_android.ui.screens.login_sign_up.fragments.name

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.dialogs.SimpleTextBottomSheetDialog
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenFragmentBase
import io.golos.cyber_android.ui.shared.extensions.safeNavigate
import io.golos.cyber_android.ui.shared.extensions.setOnDrawableEndClickListener
import io.golos.cyber_android.ui.shared.text.AllLowersInputFilter
import io.golos.cyber_android.ui.shared.utils.ViewUtils
import io.golos.cyber_android.ui.shared.utils.asEvent
import io.golos.data.errors.AppError
import io.golos.data.errors.CyberServicesError
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.use_cases.model.*
import kotlinx.android.synthetic.main.fragment_sign_up_name.*

class SignUpNameFragment : SignUpScreenFragmentBase<SignUpNameViewModel>(
    SignUpNameViewModel::class.java) {
    override val fieldToValidate: EditText?
        get() = username
    override val continueButton: View
        get() = next

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_name, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        username.filters = arrayOf(
            InputFilter.LengthFilter(viewModel.maxUserNameLen),
            AllLowersInputFilter()
        )

        ivBack.setOnClickListener { findNavController().navigateUp() }
        next.setOnClickListener {
            signUpViewModel.validateUserName(username.text.toString())
        }

        username.setOnDrawableEndClickListener {
            uiHelper.setSoftKeyboardVisibility(username, false)
            showExplanationDialog()
        }

        username?.post {
            ViewUtils.showKeyboard(username)
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()

        signUpViewModel.getUpdatingStateForStep<SetUserNameRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
            }
        })

        signUpViewModel.stateLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let {
                when (it) {
                    is VerifiedUserWithoutUserNameModel -> {
                        viewModel.getFieldIfValid()?.let { name ->
                            signUpViewModel.sendName(name)
                        }
                    }
                    is UnWrittenToBlockChainUserModel -> signUpViewModel.writeToBlockchain(it.userName, it.userId)
                    else -> { /*noop*/}

                }
            }
        })

        signUpViewModel.getUpdatingStateForStep<WriteUserToBlockChainRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
            }
        })

        signUpViewModel.lastRegisteredUser.asEvent().observe(this, Observer {
            onSuccess()
        })

        signUpViewModel.getValidateUserNameErrorLivaData.observe(this, Observer { error ->
            run {
                uiHelper.showMessage(error)
            }
        })

        signUpViewModel.getValidateUserNameSuccessLiveData.observe(this, Observer { success ->
            run {
                signUpViewModel.updateRegisterState()
            }
        })

        viewModel.validationResult.observe(this, Observer { validationResult ->
            errorText.visibility = if(validationResult.isValid) View.INVISIBLE else View.VISIBLE
            errorText.text = validationResult.erroText
        })
    }

    override fun inject() = App.injections.getBase<LoginActivityComponent>().inject(this)

    private fun onSuccess() {
        hideLoading()
        findNavController().safeNavigate(
            R.id.signUpNameFragment,
            R.id.action_signUpNameFragment_to_pinCodeFragment
        )
    }

    private fun onError(errorResult: QueryResult.Error<NextRegistrationStepRequestModel>) {
        hideLoading()
        when (errorResult.error) {
            is AppError.NameIsAlreadyInUseError -> uiHelper.showMessage(R.string.name_already_taken_error)
            else -> {
                val sysMessage = ((errorResult.error.cause as? CyberServicesError)?.error?.value as? ApiResponseError)?.error?.message
                if(sysMessage != null) {
                    uiHelper.showMessage(sysMessage)
                } else {
                    uiHelper.showMessage(R.string.unknown_error)
                }
            }
        }
    }

    private fun showExplanationDialog() =
        SimpleTextBottomSheetDialog.show(
            this@SignUpNameFragment,
            R.string.user_name_restriction_title,
            R.string.user_name_restriction_explanation,
            R.string.understand) {}
}
