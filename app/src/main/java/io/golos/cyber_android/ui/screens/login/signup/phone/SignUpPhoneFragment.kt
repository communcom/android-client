package io.golos.cyber_android.ui.screens.login.signup.phone


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import io.golos.cyber_android.R
import io.golos.cyber_android.safeNavigate
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.LoadingFragment
import io.golos.cyber_android.ui.screens.login.signup.SignUpViewModel
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.ViewUtils
import io.golos.domain.interactors.model.*
import io.golos.domain.model.QueryResult
import kotlinx.android.synthetic.main.fragment_sign_up_phone.*
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser


class SignUpPhoneFragment : LoadingFragment() {

    private lateinit var viewModel: SignUpPhoneViewModel

    private lateinit var signUpViewModel: SignUpViewModel

    private val emptyMask = UnderscoreDigitSlotsParser().parseSlots("____________")

    private val phoneMaskWatcher = MaskFormatWatcher(MaskImpl.createTerminated(emptyMask))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_phone, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        back.setOnClickListener {
            findNavController().navigateUp()
        }

        signUp.setOnClickListener {
            viewModel.getFieldIfValid()?.let {
                signUpViewModel.updateRegisterState(it)
            }
        }

        phone.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                viewModel.onFieldChanged(s.toString())
            }
        })

        listOf(countryInputLayout, country).forEach {
            it.setOnClickListener {
                ViewUtils.hideKeyboard(requireActivity())
                findNavController().navigate(R.id.action_signUpPhoneFragment_to_signUpCountryFragment)
            }
        }

        phoneMaskWatcher.installOn(phone)
    }

    private fun observeViewModel() {
        viewModel.getValidnessLiveData.observe(this, Observer {
            signUp.isEnabled = it
        })

        signUpViewModel.getUpdatingStateForStep<SendSmsForVerificationRequestModel>().observe(this, Observer {
            when (it) {
                is QueryResult.Loading -> showLoading()
                is QueryResult.Error -> onError()
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
                    is RegisteredUserModel -> navigateTo(R.id.action_signUpPhoneFragment_to_signUpKeyFragment)
                }
            }
        })

        signUpViewModel.getSelectedCountryLiveData.observe(this, Observer { countryModel ->
            country.setText(countryModel?.countryName)
            countryInputLayout.isActivated = countryModel != null

            if (countryModel != null)
                setupPhoneMask(countryModel)
            else {
                phoneMaskWatcher.setMask(MaskImpl.createTerminated(emptyMask))
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

        })
    }

    private fun navigateTo(@IdRes destination: Int) {
        hideLoading()
        findNavController().safeNavigate(
            R.id.signUpPhoneFragment,
            destination
        )
    }

    private fun onError() {
        hideLoading()
        Toast.makeText(requireContext(), "Phone error", Toast.LENGTH_SHORT).show()
    }

    private fun setupPhoneMask(countryModel: CountryModel) {
        val mask = UnderscoreDigitSlotsParser().parseSlots(
            requireContext().getString(R.string.phone_format).format(countryModel.countryPhoneCode)
        )
        phoneMaskWatcher.setMask(MaskImpl.createTerminated(mask))

//        val phonePrefix = requireContext().getString(R.string.phone_prefix_format).format(countryModel.countryPhoneCode)
//        if (!phone.text.startsWith(phonePrefix))
//            phone.setText(phonePrefix)

        ViewUtils.showKeyboard(phone)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this
        ).get(SignUpPhoneViewModel::class.java)

        signUpViewModel = ViewModelProviders.of(
            requireActivity(),
            requireContext()
                .serviceLocator
                .getSignUpViewModelFactory()
        ).get(SignUpViewModel::class.java)
    }
}
