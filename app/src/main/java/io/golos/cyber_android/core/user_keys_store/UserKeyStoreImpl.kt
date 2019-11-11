package io.golos.cyber_android.core.user_keys_store

import android.util.Log
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.utils.AuthUtils
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.UserKeyStore
import io.golos.domain.dependency_injection.Clarification
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
    override fun createKeys(userId: String, userName: String): GeneratedUserKeys = createKeys(userId, userName, generateMasterKey())

    /**
     * Generates new keys, stores and returns them
     */
    override fun createKeys(userId: String, userName: String, masterKey: String): GeneratedUserKeys {
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
     * @see A master key is not cryptographically resistant!
     */
    private fun generateMasterKey() = (UUID.randomUUID().toString() + UUID.randomUUID().toString())
        .replace("-", "")
        .substring(0..50)

    private fun generateKeys(userId: String, userName: String, masterKey: String): GeneratedUserKeys {

        val publicKeys = AuthUtils.generatePublicWiFs(userId,  masterKey, AuthType.values())
        val privateKeys = AuthUtils.generatePrivateWiFs(userId, masterKey, AuthType.values())

        Log.d("KEYS_GENERATION", "userId: $userId")
        Log.d("KEYS_GENERATION", "userName: $userName")
        Log.d("KEYS_GENERATION", "masterKey: $masterKey")
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
        ).also {
            Log.d("KEYS_GENERATION", "generatedKeys: $it")
        }
    }
}

//{
//    "username": "tst3xxiihfzq",
//    "alias": "johnston-yaeko-i",
//    "password": "uiCaodabxotBAwRxADKOFDaQXtUJloZuhSNOmYlbcfuUXOxFxZSk",
//    "phone": "+70000000001",
//    "email": "dragontimer+tst@gmail.com",
//    "owner_key": "5JbHzNYAwqk4DNrFBWFB1K2siWGrcCd59P8fvYcuJUbPjM5UVCv",
//    "active_key": "5Jt4DvP9UbdneAV6h6FBPuzPAup2gEh4eSckrACdazqUPk3yypS",
//    "posting_key": "5KMtQUtRVKksaNc8YcGm7twEsSYRi9JmePMA59b6d3Vy1SBAF9L",
//    "memo_key": "5JrzLTD1Ho1aFyjsutpp26jaeY8vCnzxcaFHt9nMocjouakh7Go",
//    "owner_public_key": "GLS5Kvy98yGfyhpMME1q3LxGfTrg8KWvsxXe52KqARWVMaGXKLTEx",
//    "active_public_key": "GLS8TAh1buc6sym8Za1rCsRpzf9yWk32oLwo6H4w38VkVeB6Y3dBt",
//    "posting_public_key": "GLS8ezThKDPUbkTfzWGBwgYzghxu2jPNcNiCi3W5PM6kN6ZeyNajG",
//    "memo_public_key": "GLS7aNjZFWTHHpMX2MuYQK8yh9WBrcWEoEPkNfSpd6fABhEgyuAZ1",
//    "testnet_id": 5,
//    "user_db_id": 20236,
//    "comment_id": 0
//}