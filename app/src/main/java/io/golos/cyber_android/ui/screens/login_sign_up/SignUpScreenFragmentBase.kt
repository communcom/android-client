package io.golos.cyber_android.ui.screens.login_sign_up

import android.os.Bundle
import android.text.Editable
import android.text.InputType.TYPE_CLASS_PHONE
import android.view.View
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.application.shared.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.shared.base.FragmentBaseCoroutines
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.shared.utils.TextWatcherBase
import io.golos.domain.dto.CountryDomain
import javax.inject.Inject

/**
 * Base fragment for all sign up screens that contains only one [EditText] to validate (provided by [fieldToValidate]) and
 * one "continue" button (provided by [continueButton]).
 */
abstract class SignUpScreenFragmentBase<VM: SignUpScreenViewModelBase>(private val clazz: Class<VM>): FragmentBaseCoroutines() {

    protected lateinit var viewModel: VM
    protected lateinit var signUpViewModel: SignUpViewModel

    @Inject
    protected lateinit var viewModelFactory: ActivityViewModelFactory

    @Inject
    internal lateinit var analyticsFacade: AnalyticsFacade

    /**
     * [EditText] which text is suppose to be validated. Can be null. If so, fragment class should
     * capture changes if field by himself and pass them to [viewModel] via [SignUpScreenViewModelBase.onFieldChanged] method.
     */
    protected open val fieldToValidate: EditText? = null

    /**
     * Button which accessibility should be controlled by [fieldToValidate]
     */
    abstract val continueButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        fieldToValidate?.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                val writenText = s.toString()
                if (fieldToValidate?.inputType == TYPE_CLASS_PHONE) {
                    val viewTag = fieldToValidate?.tag as? CountryDomain
                    viewTag?.let { tag ->
                        val phone = signUpViewModel.getNormalizedPhone(writenText)
                        viewModel.field = writenText
                        viewModel.onPhoneFieldChanged(phone, 11)
                    }
                } else {
                    viewModel.onFieldChanged(writenText)
                }
            }
        })
    }

    @CallSuper
    protected open fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(viewLifecycleOwner, Observer {
            if(it) {
                analyticsFacade.phoneNumberEntered()
            }

            continueButton.isEnabled = it
        })
    }

    protected abstract fun inject()

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(clazz)

        signUpViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(SignUpViewModel::class.java)
    }
}