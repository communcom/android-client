package io.golos.cyber_android.ui.screens.login.pin

import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import kotlinx.coroutines.withContext

class PinCodeModelImpl(
    private val dispatchersProvider: DispatchersProvider,
    private val stringsConverter: StringsConverter,
    private val encryptor: Encryptor,
    private val keyValueStorage: KeyValueStorageFacade,
    private val logger: Logger
) : PinCodeModel {

    private var primaryCode: String? = null
    private var repeatedCode: String? = null

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

    override suspend fun saveCode(): Boolean =
        withContext(dispatchersProvider.networkDispatcher) {
            try {
                val codeAsBytes = stringsConverter.toBytes(primaryCode!!)
                val encryptedCode = encryptor.encrypt(codeAsBytes)
                keyValueStorage.savePinCode(encryptedCode!!)
                true
            } catch (ex: Exception) {
                logger(ex)
                false
            }
        }
}