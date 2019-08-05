package io.golos.cyber_android.ui.screens.login_activity.signup

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.golos.cyber_android.ui.base.FragmentBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.views.utils.TextWatcherBase
import javax.inject.Inject

/**
 * Base fragment for all sign up screens that contains only one [EditText] to validate (provided by [fieldToValidate]) and
 * one "continue" button (provided by [continueButton]).
 */
abstract class SignUpScreenFragmentBase<VM: SignUpScreenViewModelBase>(private val clazz: Class<VM>): FragmentBase() {

    protected lateinit var viewModel: VM
    protected lateinit var signUpViewModel: SignUpViewModel

    @Inject
    protected lateinit var viewModelFactory: ActivityViewModelFactory

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
                viewModel.onFieldChanged(s.toString())
            }
        })
    }

    @CallSuper
    protected open fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            continueButton.isEnabled = it
        })
    }

    protected abstract fun inject()

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(clazz)

        signUpViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(SignUpViewModel::class.java)
    }
}