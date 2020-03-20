package io.golos.cyber_android.ui.screens.login_sign_up.fragments.phone


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_activity.di.LoginActivityComponent
import io.golos.cyber_android.ui.screens.login_shared.SignUpDescriptionHelper
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_up.SignUpScreenFragmentBase
import io.golos.cyber_android.ui.shared.countries.CountriesRepository
import io.golos.cyber_android.ui.shared.extensions.moveCursorToTheEnd
import io.golos.cyber_android.ui.shared.extensions.safeNavigate
import io.golos.cyber_android.ui.shared.utils.ViewUtils
import io.golos.domain.dto.CountryDomain
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.use_cases.model.*
import kotlinx.android.synthetic.main.fragment_sign_up_phone.*
import kotlinx.android.synthetic.main.layout_do_you_have_account.*
import kotlinx.coroutines.launch
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import javax.inject.Inject


class SignUpPhoneFragment : SignUpScreenFragmentBase<SignUpPhoneViewModel>(SignUpPhoneViewModel::class.java) {

    override val fieldToValidate: EditText
        get() = phone

    override val continueButton: View
        get() = signUp

    private val emptyMask = UnderscoreDigitSlotsParser().parseSlots("_")

    private val phoneMaskWatcher = MaskFormatWatcher(MaskImpl.createTerminated(emptyMask))

    @Inject
    internal lateinit var dataPass: LoginActivityFragmentsDataPass

    @Inject
    internal lateinit var countriesRepository: CountriesRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up_phone, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        header.setOnBackButtonClickListener { findNavController().navigateUp() }

        signUp.setOnClickListener {
            val phone = viewModel.field
            if(!TextUtils.isEmpty(phone)){
                signUpViewModel.updateRegisterState(phone, null)
            }
        }
        listOf(country, country).forEach {
            it.setOnClickListener {
                ViewUtils.hideKeyboard(requireActivity())
                findNavController().navigate(R.id.action_signUpPhoneFragment_to_signUpCountryFragment)
            }
        }

        phoneMaskWatcher.installOn(phone)
        phone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                phone.moveCursorToTheEnd()
            }
        }

        tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpPhoneFragment_to_signInFragment)
        }
        SignUpDescriptionHelper.formSignUpDescription(this, signUpDescription)
    }

    override fun onResume() {
        super.onResume()

        if (phone.isFocused) {
            phone.moveCursorToTheEnd()
        }

        launch {
            countriesRepository.getCurrentCountry()?.let {  }
        }

        val selectedCountry = dataPass.getSelectedCountry()
        if(selectedCountry != null) {
            onCountrySelected(selectedCountry)
        } else {
            launch {
                val currentCountry = countriesRepository.getCurrentCountry()

                currentCountry?.let {
                    analyticsFacade.countrySelected(it.available, it.code.toString())
                }

                if(currentCountry != null && currentCountry.available) {
                    dataPass.putSelectedCountry(currentCountry)
                    onCountrySelected(currentCountry)
                }
            }
        }
    }

    override fun observeViewModel() {
        super.observeViewModel()
        signUpViewModel.getUpdatingStateForStep<SendSmsForVerificationRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError(it)
                is QueryResult.Success -> hideLoading()

            }
        })

        signUpViewModel.stateLiveData.observe(this, Observer { event ->
            event.getIfNotHandled()?.let {
                when (it) {
                    is UnregisteredUserModel -> viewModel.field.let { phone ->
                        signUpViewModel.sendCodeOn(phone)
                    }
                    is UnverifiedUserModel -> navigateTo(R.id.action_signUpPhoneFragment_to_signUpVerificationFragment)
                    is VerifiedUserWithoutUserNameModel -> navigateTo(R.id.action_signUpPhoneFragment_to_signUpNameFragment)
                    is UnWrittenToBlockChainUserModel -> navigateTo(R.id.action_signUpPhoneFragment_to_signUpNameFragment)
                    is RegisteredUserModel -> {
                        uiHelper.showMessage(R.string.phone_already_taken_error)
                    }
                }
            }
        })

        signUpViewModel.getSelectedPhoneLiveData.observe(this, Observer { phoneValue ->
            phone.setText(phoneValue)
        })
    }

    override fun inject() = App.injections.getBase<LoginActivityComponent>().inject(this)

    override fun onDestroy() {
        super.onDestroy()
        signUpViewModel.resetCountrySelection()
    }

    @SuppressLint("SetTextI18n")
    private fun onCountrySelected(countryModel: CountryDomain?) {
        country.setText(countryModel?.let { "${it.emoji} ${it.name}"} ?: "")
        country.isActivated = countryModel != null

        if (countryModel != null) {
            setupPhoneMask(countryModel)
            phone.isEnabled = true
            phone.setOnClickListener(null)
            phone.tag = countryModel
        } else {
            phoneMaskWatcher.setMask(MaskImpl.createTerminated(emptyMask))
            phone.isEnabled = false
            phone.tag = null
            phone.setOnClickListener {
                ViewUtils.hideKeyboard(requireActivity())
                findNavController().navigate(R.id.action_signUpPhoneFragment_to_signUpCountryFragment)
            }
        }
    }

    private fun navigateTo(@IdRes destination: Int) {
        hideLoading()
        findNavController().safeNavigate(
            R.id.signUpPhoneFragment,
            destination
        )
    }

    private fun onError(errorResult: QueryResult.Error<NextRegistrationStepRequestModel>) {
        hideLoading()
        Toast.makeText(requireContext(), errorResult.error.message, Toast.LENGTH_SHORT).show()
    }

    private fun setupPhoneMask(countryModel: CountryDomain) {
        val phoneFormat = getString(R.string.phone_format).format(countryModel.code)
        val mask = UnderscoreDigitSlotsParser().parseSlots(phoneFormat)
        phoneMaskWatcher.setMask(MaskImpl.createTerminated(mask))
        val prefixPhoneNumber = getString(R.string.phone_prefix_format).format(countryModel.code)
        phone.prefix = prefixPhoneNumber
        ViewUtils.showKeyboard(phone)
    }
}
