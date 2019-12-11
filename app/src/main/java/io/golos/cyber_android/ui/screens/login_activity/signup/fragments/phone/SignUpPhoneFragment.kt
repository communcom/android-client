package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.phone


import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
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
import io.golos.cyber_android.ui.utils.ViewUtils
import io.golos.cyber_android.ui.utils.openWebPage
import io.golos.domain.dto.CountryEntity
import io.golos.domain.requestmodel.QueryResult
import io.golos.domain.use_cases.model.*
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

        signUp.setOnClickListener {
            val phone = viewModel.field
            if(!TextUtils.isEmpty(phone)){
                signUpViewModel.updateRegisterState(phone)
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

        formSignUpDescription()
    }

    override fun onResume() {
        super.onResume()

        if (phone.isFocused) {
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

    override fun onDestroy() {
        super.onDestroy()
        signUpViewModel.resetCountrySelection()
    }

    private fun onCountrySelected(countryModel: CountryEntity?) {
        country.setText(countryModel?.countryName)
        country.isActivated = countryModel != null

        if (countryModel != null) {
            setupPhoneMask(countryModel)
            phone.isFocusable = true
            phone.setOnClickListener(null)
            phone.tag = countryModel
        } else {
            phoneMaskWatcher.setMask(MaskImpl.createTerminated(emptyMask))
            phone.isFocusable = false
            phone.tag = null
            phone.setOnClickListener {
                ViewUtils.hideKeyboard(requireActivity())
                findNavController().navigate(R.id.action_signUpPhoneFragment_to_signUpCountryFragment)
            }
        }

        val countryFlag = requireContext().resources.getDimension(R.dimen.sign_up_country_flag_size).toInt()
        Glide.with(requireContext())
            .load(countryModel?.thumbNailUrl)
            .apply(RequestOptions.circleCropTransform().override(countryFlag, countryFlag))
            .into(object : CustomViewTarget<TextView, Drawable>(country) {
                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    country.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null)
                    country.compoundDrawablePadding = requireContext().resources.getDimensionPixelSize(R.dimen.margin_small)
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

    private fun formSignUpDescription() {
        val descriptionSpannable = SpannableStringBuilder(getString(R.string.sign_up_description))
        val linkColor = ContextCompat.getColor(requireContext(), R.color.blue)

        val privacyPolicySpannable = SpannableStringBuilder(getString(R.string.sign_up_description_1))
        val privacyPolicyClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebPage("https://commun.com/privacy")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        privacyPolicySpannable.setSpan(
            privacyPolicyClick,
            0,
            privacyPolicySpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        privacyPolicySpannable.setSpan(
            ForegroundColorSpan(linkColor),
            0,
            privacyPolicySpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val andSpannable = SpannableStringBuilder(getString(R.string.sign_up_description_2))

        val blockchainSpannable = SpannableStringBuilder(getString(R.string.sign_up_description_3))
        val blockchainClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebPage("https://commun.com/agreement")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        blockchainSpannable.setSpan(
            blockchainClick,
            0,
            blockchainSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        blockchainSpannable.setSpan(
            ForegroundColorSpan(linkColor),
            0,
            blockchainSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        signUpDescription.text = SpannableStringBuilder(
            TextUtils.concat(
                descriptionSpannable,
                " ",
                privacyPolicySpannable,
                " ",
                andSpannable,
                " ",
                blockchainSpannable
            )
        )
        signUpDescription.movementMethod = LinkMovementMethod.getInstance()
        signUpDescription.highlightColor = Color.TRANSPARENT
    }
}
