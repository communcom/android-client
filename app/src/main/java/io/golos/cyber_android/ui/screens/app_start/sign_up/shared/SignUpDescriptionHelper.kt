package io.golos.cyber_android.ui.screens.app_start.sign_up.shared

import android.content.res.Configuration
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.shared.utils.getStyledAttribute
import io.golos.cyber_android.ui.shared.utils.openWebPage
import io.golos.domain.GlobalConstants

object SignUpDescriptionHelper {
    fun formSignUpDescription(fragment: Fragment, signUpDescription: TextView) {
        val context = signUpDescription.context

        val descriptionSpannable = SpannableStringBuilder(context.getString(R.string.sign_up_description))
        val descriptionColor= when {
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_DARK -> ContextCompat.getColor(context,R.color.white)
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_LIGHT -> ContextCompat.getColor(context,R.color.grey)
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(context,R.color.white)
            else -> ContextCompat.getColor(context,R.color.grey)
        }
        descriptionSpannable.setSpan(
            ForegroundColorSpan(descriptionColor),
            0,
            descriptionSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val linkColor = ContextCompat.getColor(context, R.color.blue)

        val privacyPolicySpannable = SpannableStringBuilder(context.getString(R.string.sign_up_description_1))
        val privacyPolicyClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                fragment.openWebPage("https://commun.com/doc/privacy")
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

        val andSpannable = SpannableStringBuilder(context.getString(R.string.sign_up_description_2))
        andSpannable.setSpan(
            ForegroundColorSpan(descriptionColor),
            0,
            andSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val blockchainSpannable = SpannableStringBuilder(context.getString(R.string.sign_up_description_3))
        val blockchainClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                fragment.openWebPage("https://commun.com/doc/agreement")
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