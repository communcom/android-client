package io.golos.cyber_android.ui.screens.app_start.sign_up.phone.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.gms.safetynet.SafetyNet
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignUpPhoneBinding
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.SignUpDescriptionHelper
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.di.SignUpPhoneFragmentComponent
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToCountriesListCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToPhoneVerificationCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.ShowCaptchaCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.view_model.SignUpPhoneViewModel
import io.golos.cyber_android.ui.shared.extensions.moveCursorToTheEnd
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.utils.ViewUtils
import io.golos.domain.dto.CountryDomain
import kotlinx.android.synthetic.main.fragment_sign_up_phone.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import timber.log.Timber

class SignUpPhoneFragment : FragmentBaseMVVM<FragmentSignUpPhoneBinding, SignUpPhoneViewModel>() {
    private val emptyMask = UnderscoreDigitSlotsParser().parseSlots("_")
    private val phoneMaskWatcher = MaskFormatWatcher(MaskImpl.createTerminated(emptyMask))

    override val isBackHandlerEnabled: Boolean = true

    override fun provideViewModelType(): Class<SignUpPhoneViewModel> = SignUpPhoneViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_up_phone

    override fun inject(key: String) = App.injections.get<SignUpPhoneFragmentComponent>(key).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<SignUpPhoneFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentSignUpPhoneBinding, viewModel: SignUpPhoneViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneMaskWatcher.installOn(phone)

        header.setOnBackButtonClickListener { viewModel.onBackClick() }
        country.setOnClickListener { viewModel.onCountyFieldClick() }
        signUp.setOnClickListener { viewModel.onNextButtonClick() }

        phone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                phone.moveCursorToTheEnd()
            }
        }

        viewModel.currentCountry.observe({viewLifecycleOwner.lifecycle}) { processCountySelected(it) }

        SignUpDescriptionHelper.formSignUpDescription(this, signUpDescription)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> findNavController().navigateUp()
            is HideKeyboardCommand -> uiHelper.setSoftKeyboardVisibility(phone, false)
            is NavigateToCountriesListCommand -> moveToCountriesList()
            is NavigateToPhoneVerificationCommand -> moveToPhoneVerification()
            is NavigateToSelectSignUpMethodCommand -> moveToSelectMethod()
            is ShowCaptchaCommand -> startCaptcha()

            else -> throw UnsupportedOperationException("This command is not supported")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onActive()
    }

    @SuppressLint("SetTextI18n")
    private fun processCountySelected(countryModel: CountryDomain) {
        country.setText(countryModel.let { "${it.emoji} ${it.name}"} ?: "")
        country.isActivated = true
        setupPhoneMask(countryModel)
    }

    private fun setupPhoneMask(countryModel: CountryDomain) {
        val phoneFormat = getString(R.string.phone_format).format(countryModel.code)
        val mask = UnderscoreDigitSlotsParser().parseSlots(phoneFormat)
        phoneMaskWatcher.setMask(MaskImpl.createTerminated(mask))
        val prefixPhoneNumber = getString(R.string.phone_prefix_format).format(countryModel.code)

        phone.prefix = prefixPhoneNumber
        phone.requestFocus()

        phone.post { ViewUtils.showKeyboard(phone) }
    }

    private fun moveToCountriesList() = findNavController().navigate(R.id.action_signUpPhoneFragment2_to_signUpCountryFragment2)

    private fun moveToPhoneVerification() = findNavController().navigate(R.id.action_signUpPhoneFragment2_to_signUpVerificationFragment2)

    private fun moveToSelectMethod() = findNavController().navigate(R.id.action_signUpPhoneFragment2_to_signUpSelectMethodFragment)

    private fun startCaptcha() {
        SafetyNet.getClient(requireActivity()).verifyWithRecaptcha(BuildConfig.GOOGLE_RECAPTCHA_KEY)
            .addOnSuccessListener { response ->
                val userResponseToken = response.tokenResult
                if (userResponseToken?.isNotEmpty() == true) {
                    viewModel.onCaptchaReceived(userResponseToken)
                } else {
                    uiHelper.showMessage(R.string.common_captcha_error)
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e)
                uiHelper.showMessage(R.string.common_captcha_error)
            }
    }
}