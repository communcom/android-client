package io.golos.cyber_android.core.user_keys_store

import io.golos.cyber4j.model.AuthType
import io.golos.cyber4j.utils.AuthUtils
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import io.golos.domain.interactors.model.GeneratedUserKeys
import java.util.*
import javax.inject.Inject

class UserKeyStoreImpl
@Inject
constructor(
    private val keyValueStorage: KeyValueStorageFacade,
    private val stringsConverter: StringsConverter,
    private val encryptor: Encryptor
): UserKeyStore {
    /**
     * Generates new keys, stores and returns them
     */
    override fun createKeys(userName: String): GeneratedUserKeys = createKeys(userName, generateMasterKey())

    /**
     * Generates new keys, stores and returns them
     */
    override fun createKeys(userName: String, masterKey: String): GeneratedUserKeys {
        val keys = generateKeys(userName, masterKey)

        listOf(
            UserKey(UserKeyType.MASTER, keys.masterPassword),
            UserKey(UserKeyType.ACTIVE, keys.activePrivateKey),
            UserKey(UserKeyType.MEMO, keys.memoPrivateKey),
            UserKey(UserKeyType.OWNER, keys.ownerPrivateKey),
            UserKey(UserKeyType.POSTING, keys.postingPrivateKey)
        )
        .apply { updateKeys(this) }

        return keys
    }

    /**
     * Returns private part of a key (in case of master - key itself)
     */
    override fun getKey(keyType: UserKeyType): String =
        keyValueStorage.getUserKey(keyType)!!
            .let { encryptor.decrypt(it)!! }
            .let { stringsConverter.fromBytes(it) }

    /**
     * Updates keys in a storage
     */
    override fun updateKeys(keys: List<UserKey>) =
        keys.forEach { key ->
            stringsConverter.toBytes(key.key)
                .let { encryptor.encrypt(it)!! }
                .let { keyValueStorage.saveUserKey(it, key.keyType) }
        }

    /**
     * @see A master key is not cryptographically resistant!
     */
    private fun generateMasterKey() = (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "")

    private fun generateKeys(userName: String, masterKey: String): GeneratedUserKeys {
        val publicKeys = AuthUtils.generatePublicWiFs(userName,  masterKey, AuthType.values())
        val privateKeys = AuthUtils.generatePrivateWiFs(userName, masterKey, AuthType.values())

        return GeneratedUserKeys(
            userName,
            masterKey,
            publicKeys.getValue(AuthType.OWNER),
            privateKeys.getValue(AuthType.OWNER),
            publicKeys.getValue(AuthType.ACTIVE),
            privateKeys.getValue(AuthType.ACTIVE),
            publicKeys.getValue(AuthType.POSTING),
            privateKeys.getValue(AuthType.POSTING),
            publicKeys.getValue(AuthType.MEMO),
            privateKeys.getValue(AuthType.MEMO)
        )
    }
}