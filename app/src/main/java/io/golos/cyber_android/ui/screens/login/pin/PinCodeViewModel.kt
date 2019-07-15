package io.golos.cyber_android.ui.screens.login.pin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.SingleLiveData
import io.golos.cyber_android.ui.screens.login.pin.view_state_dto.CodeState

class PinCodeViewModel(private val model: PinCodeModel): ViewModel() {
    val isInExtendedMode = MutableLiveData<Boolean>(false)

    val codeState = MutableLiveData<CodeState>(CodeState(true, false, false, false))

    val error: SingleLiveData<Int> = SingleLiveData()

    fun onPrimaryCodeUpdated(code: String?) {
        model.updatePrimaryCode(code)

        if(model.isPrimaryCodeCompleted()) {
            isInExtendedMode.value = true
            codeState.value = CodeState(false, true, codeState.value!!.isInErrorState, false)
        }
    }

    fun onRepeatedCodeUpdated(code: String?) {
        model.updateRepeatedCode(code)

        if(model.isRepeatedCodeCompleted()) {
            if(model.validate()) {
                codeState.value = CodeState(false, true, false, false)

                // model.save()
                // navigate() fake
            } else {
                model.updatePrimaryCode(null)
                model.updateRepeatedCode(null)

                error.value = R.string.codes_not_match
                codeState.value = CodeState(true, false, true, true)
            }
        }
    }
}