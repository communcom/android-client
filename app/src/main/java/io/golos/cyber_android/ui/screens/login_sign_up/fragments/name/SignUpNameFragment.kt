package io.golos.cyber_android.ui.screens.login_sign_up.fragments.name

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.shared.extensions.safeNavigate
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenFragmentBase
import io.golos.cyber_android.ui.shared.utils.asEvent
import io.golos.cyber_android.ui.shared.utils.AllLowersInputFilter
import io.golos.cyber_android.ui.shared.utils.ViewUtils
import io.golos.data.errors.AppError
import io.golos.domain.use_cases.model.*
import io.golos.domain.requestmodel.QueryResult
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
            InputFilter.LengthFilter(SignUpNameViewModel.MAX_USERNAME_LENGTH),
            AllLowersInputFilter()
        )

        ivBack.setOnClickListener { findNavController().navigateUp() }
        next.setOnClickListener {
            signUpViewModel.validateUserName(username.text.toString())
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
        val errorMsg = when (errorResult.error) {
            is AppError.NameIsAlreadyInUseError -> R.string.name_already_taken_error
            else -> R.string.unknown_error
        }
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
    }
}
