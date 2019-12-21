package io.golos.cyber_android.core.user_keys_store

import io.golos.commun4j.model.AuthType
import io.golos.commun4j.utils.AuthUtils
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.UserKeyStore
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.UserKey
import io.golos.domain.dto.UserKeyType
import io.golos.domain.use_cases.model.GeneratedUserKeys
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class UserKeyStoreImpl
@Inject
constructor(
    private val keyValueStorage: KeyValueStorageFacade,
    private val stringsConverter: StringsConverter,
    @Named(Clarification.AES) private val encryptor: Encryptor
): UserKeyStore {

    /**
     * Generates new keys, stores and returns them
     */
    override fun createKeys(userId: UserIdDomain, userName: String): GeneratedUserKeys =
        createKeys(userId, userName, generateMasterKey())

    /**
     * Generates new keys, stores and returns them
     */
    override fun createKeys(userId: UserIdDomain, userName: String, masterKey: String): GeneratedUserKeys {
        val keys = generateKeys(userId, userName, masterKey)

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
     * Removes all keys
     */
    override fun clearAllKeys() {
        listOf(
            UserKeyType.MASTER,
            UserKeyType.ACTIVE,
            UserKeyType.MEMO,
            UserKeyType.OWNER,
            UserKeyType.POSTING
        )
        .forEach { keyValueStorage.removeUserKey(it) }
    }

    /**
     * @see A master key is not cryptographically resistant!
     */
    private fun generateMasterKey() = (UUID.randomUUID().toString() + UUID.randomUUID().toString())
        .replace("-", "")
        .substring(0..50)

    private fun generateKeys(userId: UserIdDomain, userName: String, masterKey: String): GeneratedUserKeys {

        val publicKeys = AuthUtils.generatePublicWiFs(userId.userId,  masterKey, AuthType.values())
        val privateKeys = AuthUtils.generatePrivateWiFs(userId.userId, masterKey, AuthType.values())

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