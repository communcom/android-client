package io.golos.cyber_android.ui.screens.login.pin

import io.golos.domain.Persister

class PinCodeModelImpl(private val persister: Persister): PinCodeModel {
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

    override suspend fun saveCode() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}