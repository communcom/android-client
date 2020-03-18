package io.golos.cyber_android.ui.screens.login_sign_up_select_method

import android.graphics.Color
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.utils.openWebPage
import kotlinx.android.synthetic.main.fragment_sign_up_phone.*
import kotlinx.android.synthetic.main.fragment_sign_up_phone.header
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.*
import kotlinx.android.synthetic.main.fragment_sign_up_select_method.signUpDescription

class SignUpSelectMethodFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_select_method, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.setOnBackButtonClickListener { findNavController().navigateUp() }

        phoneButton.setOnClickListener { findNavController().navigate(R.id.action_signUpSelectMethod_to_signUpPhoneFragment) }
        googleButton.setOnClickListener {  }
        facebookButton.setOnClickListener {  }

        tvSignIn.setOnClickListener { findNavController().navigate(R.id.action_signUpSelectMethod_to_signInFragment) }

        formSignUpDescription()
    }

    private fun formSignUpDescription() {
        val descriptionSpannable = SpannableStringBuilder(getString(R.string.sign_up_description))
        val descriptionColor= ContextCompat.getColor(requireContext(), R.color.grey)
        descriptionSpannable.setSpan(
            ForegroundColorSpan(descriptionColor),
            0,
            descriptionSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val linkColor = ContextCompat.getColor(requireContext(), R.color.blue)

        val privacyPolicySpannable = SpannableStringBuilder(getString(R.string.sign_up_description_1))
        val privacyPolicyClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebPage("https://commun.com/doc/privacy")
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
        andSpannable.setSpan(
            ForegroundColorSpan(descriptionColor),
            0,
            andSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val blockchainSpannable = SpannableStringBuilder(getString(R.string.sign_up_description_3))
        val blockchainClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebPage("https://commun.com/doc/agreement")
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