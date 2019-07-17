package io.golos.cyber_android.application.dependency_injection

import dagger.Binds
import dagger.Module
import io.golos.cyber_android.application.dependency_injection.scopes.ApplicationScope
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorFingerprint
import io.golos.cyber_android.core.encryption.rsa.EncryptorRSA
import io.golos.cyber_android.core.key_value_storage.KeyValueStorageFacadeImpl
import io.golos.cyber_android.core.key_value_storage.storages.Storage
import io.golos.cyber_android.core.key_value_storage.storages.StorageOperationsInstance
import io.golos.cyber_android.core.key_value_storage.storages.combined.CombinedStorage
import io.golos.cyber_android.core.key_value_storage.storages.in_memory.InMemoryStorage
import io.golos.cyber_android.core.key_value_storage.storages.shared_preferences.SharedPreferencesStorage
import io.golos.cyber_android.core.strings_converter.StringsConverterImpl
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.rules.CyberPostToEntityMapper
import io.golos.domain.rules.VoteRequestModelToEntityMapper
import javax.inject.Named

@Suppress("unused")
@Module
abstract class AppModuleBinds {
    //region Key-value storage
    @Binds
    abstract fun provideKeyValueStorageFacade(facade: KeyValueStorageFacadeImpl): KeyValueStorageFacade

    @Binds
    abstract fun provideKeyValueStorage(storage: CombinedStorage): Storage

    @Binds
    @ApplicationScope
    @Named(Clarification.CACHE)
    abstract fun provideCacheStorage(storage: InMemoryStorage): StorageOperationsInstance

    @Binds
    @Named(Clarification.PERSISTENT)
    abstract fun providePersistentStorage(storage: SharedPreferencesStorage): StorageOperationsInstance
    // endregion

    // region Encryption
    @Binds
    @Named(Clarification.RSA)
    abstract fun provideEncryptor(encryptor: EncryptorRSA): Encryptor

    @Binds
    abstract fun provideEncryptorForFingerprint(encryptor: EncryptorAES): EncryptorFingerprint
    // endregion

    @Binds
    abstract fun provideStringsConverter(converter: StringsConverterImpl): StringsConverter

    @Binds
    abstract fun provideCyberPostToEntityMapper(mapper: CyberPostToEntityMapper): CyberPostToEntityMapper

    @Binds
    abstract fun provideVoteToEntityMapper(mapper: VoteRequestModelToEntityMapper): VoteRequestModelToEntityMapper
}