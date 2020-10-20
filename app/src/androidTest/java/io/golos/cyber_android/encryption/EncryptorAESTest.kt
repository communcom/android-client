package io.golos.cyber_android.encryption

import io.golos.cyber_android.application.shared.encryption.rsa.EncryptorRSA
import io.golos.cyber_android.application.shared.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.cyber_android.application.shared.key_value_storage.storages.in_memory.InMemoryStorage

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