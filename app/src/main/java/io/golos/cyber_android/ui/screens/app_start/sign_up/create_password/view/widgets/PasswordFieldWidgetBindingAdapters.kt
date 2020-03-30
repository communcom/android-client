package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.golos.cyber_android.ui.shared.extensions.parentActivity

@BindingAdapter("password_field_widget_text_visibility")
fun setCasesBinding(view: PasswordFieldWidget, valueToBind: LiveData<Boolean>?) {
    valueToBind?.let { liveValue ->
        view.parentActivity?.let { activity ->
            liveValue.observe(activity, Observer { view.setTextVisibility(it) })
        }
    }
}
