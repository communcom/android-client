package io.golos.cyber_android.encryption

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.golos.domain.Encryptor
import io.golos.cyber_android.application.shared.encryption.rsa.EncryptorRSA
import io.golos.domain.StringsConverter
import io.golos.data.strings_converter.StringsConverterImpl
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptorRSATest: EncryptorTestBase() {

    companion object {
        private lateinit var converterInstance: StringsConverter
        private lateinit var encryptionUtilsInstance: Encryptor

        @BeforeClass
        @JvmStatic
        fun setUp() {
            converterInstance = StringsConverterImpl()
            encryptionUtilsInstance =
                    EncryptorRSA(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
        }
    }

    override val converter: StringsConverter
        get() = converterInstance

    override val encryptionUtils: Encryptor
        get() = encryptionUtilsInstance
}