package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.pin_code_storage

import javax.inject.Inject

class PinCodesStorageImpl
@Inject
constructor() : PinCodesStorage {
    private var primaryCode: String? = null
    private var repeatedCode: String? = null

    override val pinCode: String?
        get() = primaryCode

    override fun updatePrimaryCode(code: String?) {
        primaryCode = code
    }

    override fun updateRepeatedCode(code: String?) {
        repeatedCode = code
    }

    override fun isPrimaryCodeCompleted() = primaryCode != null

    override fun isRepeatedCodeCompleted() = repeatedCode != null

    /**
     * @return true if valid
     */
    override fun validate() = primaryCode == repeatedCode
}