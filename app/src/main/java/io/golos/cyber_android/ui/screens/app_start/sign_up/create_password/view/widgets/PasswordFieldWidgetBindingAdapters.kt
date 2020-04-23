package io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view.widgets

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("password_field_widget_text_visibility")
fun setCasesBinding(view: PasswordFieldWidget, valueToBind: LiveData<Boolean>?) {
    valueToBind?.value?.let { view.setTextVisibility(it) }
}
