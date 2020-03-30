package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.dto.PasswordValidationCase
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("password_validation_min_len")
fun setMinLenBinding(view: PasswordValidationWidget, valueToBind: Int) {
    view.minLen = valueToBind
}

@BindingAdapter("password_validation_cases")
fun setCasesBinding(view: PasswordValidationWidget, valueToBind: LiveData<List<PasswordValidationCase>>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.updateCases(it) })
        }
    }
}