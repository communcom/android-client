package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.phone


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.login_activity.LoginActivityComponent
import io.golos.cyber_android.ui.common.extensions.moveCursorToTheEnd
import io.golos.cyber_android.ui.common.extensions.safeNavigate
import io.golos.cyber_android.ui.screens.login_activity.signup.SignUpScreenFragmentBase
import io.golos.cyber_android.ui.common.utils.ViewUtils
import io.golos.domain.dto.CountryEntity
import io.golos.domain.use_cases.model.*
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.fragment_sign_up_phone.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher


class SignUpPhoneFragment : SignUpScreenFragmentBase<SignUpPhoneViewModel>(SignUpPhoneViewModel::class.java) {

    override val fieldToValidate: EditText
        get() = phone

    override val continueButton: View
        get() = signUp

    private val emptyMask = UnderscoreDigitSlotsParser().parseSlots("_")

    private val phoneMaskWatcher = MaskFormatWatcher(MaskImpl.createTerminated(emptyMask))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sign_up_phone, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        //TODO kv 29.10/2019 раскоментировать блок когда понадобиться капча в приложении
        signUp.setOnClickListener {
            viewModel.getFieldIfValid()?.let {
                /*val captchaClient: SafetyNetClient = SafetyNet.getClient(requireActivity())
                captchaClient
                    .verifyWithRecaptcha("6LdIAcAUAAAAANtWk2WpvZ0jMqX58NB3QDpgZR2S")
                    .addOnSuccessListener { successEvent ->
                        val tokenCaptcha: String = successEvent.tokenResult
                        signUpViewModel.updateRegisterState(it)

                        // More code here
                    }
                    .addOnFailureListener {
                        Timber.e(it)
                    }*/
                signUpViewModel.updateRegisterState(it)
            }
        }
        listOf(countryInputLayout, country).forEach {
            it.setOnClickListener {
                ViewUtils.hideKeyboard(requireActivity())
                findNavController().navigate(R.id.action_signUpPhoneFragment_to_signUpCountryFragment)
            }
        }

        phoneMaskWatcher.installOn(phone)
        phone.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                phone.moveCursorToTheEnd()
            }
        }

        signUpViewModel.initSelectedCountry()
    }

    override fun onResume() {
        super.onResume()

        if(phone.isFocused) {
            phone.moveCursorToTheEnd()
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
                    is UnregisteredUserModel -> viewModel.getFieldIfValid()?.let { phone ->
                        signUpViewModel.sendCodeOn(phone)
                    }
                    is UnverifiedUserModel -> navigateTo(R.id.action_signUpPhoneFragment_to_signUpVerificationFragment)
                    is VerifiedUserWithoutUserNameModel -> navigateTo(R.id.action_signUpPhoneFragment_to_signUpNameFragment)
                    is UnWrittenToBlockChainUserModel -> navigateTo(R.id.action_signUpPhoneFragment_to_signUpNameFragment)
                    is RegisteredUserModel -> {
                        Toast.makeText(requireContext(), R.string.phone_already_taken_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        signUpViewModel.getSelectedCountryLiveData.observe(this, Observer { countryModel ->
            onCountrySelected(countryModel)
        })

        signUpViewModel.getSelectedPhoneLiveData.observe(this, Observer { phoneValue ->
            phone.setText(phoneValue)
        })
    }

    override fun inject() = App.injections.get<LoginActivityComponent>().inject(this)

    private fun onCountrySelected(countryModel: CountryEntity?) {
        country.setText(countryModel?.countryName)
        countryInputLayout.isActivated = countryModel != null

        if (countryModel != null) {
            setupPhoneMask(countryModel)
            phone.isEnabled = true
            phoneInputLayout.isEnabled = true
        } else {
            phoneMaskWatcher.setMask(MaskImpl.createTerminated(emptyMask))
            phone.isEnabled = false
            phoneInputLayout.isEnabled = false
        }


        Glide.with(requireContext())
            .load(countryModel?.thumbNailUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(object : CustomViewTarget<TextView, Drawable>(country) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    country.setCompoundDrawablesWithIntrinsicBounds(null, null, resource, null)
                }

            })
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

    private fun setupPhoneMask(countryModel: CountryEntity) {
        val phoneFormat = getString(R.string.phone_format).format(countryModel.countryPhoneCode)
        val mask = UnderscoreDigitSlotsParser().parseSlots(phoneFormat)
        phoneMaskWatcher.setMask(MaskImpl.createTerminated(mask))
        val prefixPhoneNumber = getString(R.string.phone_prefix_format).format(countryModel.countryPhoneCode)
        phone.prefix = prefixPhoneNumber
        ViewUtils.showKeyboard(phone)
    }
}
