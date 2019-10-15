package io.golos.cyber_android.encryption

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.golos.domain.Encryptor
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorAESOldApi
import io.golos.cyber_android.core.encryption.rsa.EncryptorRSA
import io.golos.cyber_android.core.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.cyber_android.core.key_value_storage.storages.in_memory.InMemoryStorage
import io.golos.domain.StringsConverter
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import org.junit.BeforeClass
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
//@RequiresApi(Build.VERSION_CODES.M)
//class EncryptorAESTest: EncryptorTestBase() {
//    companion object {
//        private lateinit var converterInstance: StringsConverter
//        private lateinit var encryptionUtilsInstance: Encryptor
//
//        @BeforeClass
//        @JvmStatic
//        fun setUp() {
//            converterInstance = StringsConverterImpl()
//
//            encryptionUtilsInstance = if(Build.VERSION.SDK_INT >= 23) {     // New encryptor
//                EncryptorAES()
//            } else {                                                          // Old encryptor
//                val encryptor = EncryptorRSA(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
//
//                val storage = InMemoryStorage()
//                val storageFacade = KeyValueStorageFacadeImpl(storage, converterInstance)
//
//                EncryptorAESOldApi(storageFacade, encryptor)
//            }
//        }
//    }
//
//    override val converter: StringsConverter
//        get() = converterInstance
//
//    override val encryptionUtils: Encryptor
//        get() = encryptionUtilsInstance
//}