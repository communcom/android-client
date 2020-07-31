package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.dto.PasswordValidationCase
import io.golos.cyber_android.ui.shared.utils.getFormattedString
import io.golos.cyber_android.ui.shared.utils.getStyledAttribute
import io.golos.utils.getColorRes
import kotlinx.android.synthetic.main.view_login_password_validation.view.*

class PasswordValidationWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onCaseClickListener: ((PasswordValidationCase) -> Unit)? = null

    var minLen = 0
    set(value) {
        field = value
        minLenLabel.text = context.resources.getFormattedString(R.string.password_validation_min_len, field)
    }

    init {
        inflate(context, R.layout.view_login_password_validation, this)
        minLen = 8
        updateCases(listOf())

        lowerLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.LOWER) }
        lowerHintLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.LOWER) }

        upperLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.UPPER) }
        upperHintLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.UPPER) }

        numberLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.DIGIT) }
        numberHintLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.DIGIT) }

        minLenLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.MIN_LEN) }
        minLenHintLabel.setOnClickListener { onCaseClickListener?.invoke(PasswordValidationCase.MIN_LEN) }
    }

    fun updateCases(validCases: List<PasswordValidationCase>) {
        val validColor = context.resources.getColorRes(R.color.blue)
        val invalidColor = getStyledAttribute(R.attr.grey)

        (if(validCases.contains(PasswordValidationCase.LOWER)) validColor else invalidColor)
            .let {
                lowerLabel.setTextColor(it)
                lowerHintLabel.setTextColor(it)
            }

        (if(validCases.contains(PasswordValidationCase.UPPER)) validColor else invalidColor)
            .let {
                upperLabel.setTextColor(it)
                upperHintLabel.setTextColor(it)
            }

        (if(validCases.contains(PasswordValidationCase.DIGIT)) validColor else invalidColor)
            .let {
                numberLabel.setTextColor(it)
                numberHintLabel.setTextColor(it)
            }

        (if(validCases.contains(PasswordValidationCase.MIN_LEN)) validColor else invalidColor)
            .let {
                minLenLabel.setTextColor(it)
                minLenHintLabel.setTextColor(it)
            }
    }

    fun setOnCaseClickListener(listener: ((PasswordValidationCase) -> Unit)?) {
        onCaseClickListener = listener
    }
}