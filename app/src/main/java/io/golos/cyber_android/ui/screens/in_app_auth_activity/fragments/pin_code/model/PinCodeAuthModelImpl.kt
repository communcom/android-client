package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.pin_code.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.cyber_android.ui.shared.widgets.pin.Digit
import io.golos.domain.DispatchersProvider
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.fingerprint.FingerprintAuthManager
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class PinCodeAuthModelImpl
@Inject
constructor(
    private val fingerprintAuthManager: FingerprintAuthManager,
    private val keyValueStorage: KeyValueStorageFacade,
    private val dispatchersProvider: DispatchersProvider,
    @Named(Clarification.AES) private val encryptor: Encryptor,
    private val stringsConverter: StringsConverter
) : ModelBaseImpl(), PinCodeAuthModel {

    private lateinit var pinCode: String

    private val digits = mutableListOf<Digit>()

    override val isFingerprintAuthPossible: Boolean
        get() = fingerprintAuthManager.isAuthenticationPossible

    override suspend fun initModel() {
        withContext(dispatchersProvider.ioDispatcher) {
            pinCode = keyValueStorage.getPinCode()!!
                .let { encryptor.decrypt(it)!! }
                .let { stringsConverter.fromBytes(it) }
        }
    }

    override fun processDigit(digit: Digit): PinCodeValidationResult {
        digits.add(digit)

        if(digits.size != 4) {
            return PinCodeValidationResult.NOT_COMPLETED
        }

        val currentCode = digits.map { it.value.toString() }.joinToString("")
        return if(currentCode == pinCode) PinCodeValidationResult.VALID else PinCodeValidationResult.INVALID
    }

    override fun reset() {
        digits.clear()
    }
}