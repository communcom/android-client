package io.golos.cyber_android.core.user_keys_store

import android.util.Log
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.utils.AuthUtils
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.StringsConverter
import io.golos.domain.UserKeyStore
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import io.golos.domain.interactors.model.GeneratedUserKeys
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
    private fun generateMasterKey() = (UUID.randomUUID().toString() + UUID.randomUUID().toString())
        .replace("-", "")
        .substring(0..50)

    private fun generateKeys(userName: String, masterKey: String): GeneratedUserKeys {
        val publicKeys = AuthUtils.generatePublicWiFs(userName,  masterKey, AuthType.values())
        val privateKeys = AuthUtils.generatePrivateWiFs(userName, masterKey, AuthType.values())

        Log.d("KEYS_GENERATION", "userName: $userName")
        Log.d("KEYS_GENERATION", "masterKey: $masterKey")
        return GeneratedUserKeys(
            userName,
            masterKey,
//            publicKeys.getValue(AuthType.OWNER),
//            privateKeys.getValue(AuthType.OWNER),
//            publicKeys.getValue(AuthType.ACTIVE),
//            privateKeys.getValue(AuthType.ACTIVE),
//            publicKeys.getValue(AuthType.POSTING),
//            privateKeys.getValue(AuthType.POSTING),
//            publicKeys.getValue(AuthType.MEMO),
//            privateKeys.getValue(AuthType.MEMO)
            // todo[AS] temporary!!!!!
            "GLS86oPZfmRTTeitrJD3VcymSxfmuyPb1zqNi8ShQ4uyQ6714TgdJ",
            "5JGbtBTQTJg6jnjowxfLeF3BG5FirnVqhYPw3XKRR6LUvUVdBfK",
            "GLS7WQvt15rdAgYXgUrh1sBXZcKjLfArqo1kgUBPuJS9jUtNwh9zg",
            "5JkFtYDEuPQCcxNm27o8n4ErubajUB5Sqag2KdKnYHwMx3DWJ23",
            "GLS6iWFtxbh9N9RownYz26fAberutBrqxvcB84xsqPnc7YCZNWgBN",
            "5HxpWGeEpWxHKuS1RNcaUP3dTzDiLw7oNjdMVRtUgKYrMqtSRWW",
            "GLS5A77uJ8GVH5VVzYVHs9jdhKd4TkMArqreenedsPwdncRFn3iCV",
            "5KX4ybZ49jSLhuUDLpa15S5mkXAwaSCmFgzvs7rx33xHJDq1HPk"
        ).also {
            Log.d("KEYS_GENERATION", "generatedKeys: $it")
        }
    }
}

/*
{
    "username": "tst2elpyxqzd",
    "alias": "runolfsson-elliot-v",
    "password": "NCylnTBrIRwkaCSXwjnFXylvBHPfZIFuMIvMjNeylcKrVdVoGvOL",
    "phone": "+70000000001",
    "email": "dragontimer+tst@gmail.com",
    "owner_key": "5JGbtBTQTJg6jnjowxfLeF3BG5FirnVqhYPw3XKRR6LUvUVdBfK",
    "active_key": "5JkFtYDEuPQCcxNm27o8n4ErubajUB5Sqag2KdKnYHwMx3DWJ23",
    "posting_key": "5HxpWGeEpWxHKuS1RNcaUP3dTzDiLw7oNjdMVRtUgKYrMqtSRWW",
    "memo_key": "5KX4ybZ49jSLhuUDLpa15S5mkXAwaSCmFgzvs7rx33xHJDq1HPk",
    "owner_public_key": "GLS86oPZfmRTTeitrJD3VcymSxfmuyPb1zqNi8ShQ4uyQ6714TgdJ",
    "active_public_key": "GLS7WQvt15rdAgYXgUrh1sBXZcKjLfArqo1kgUBPuJS9jUtNwh9zg",
    "posting_public_key": "GLS6iWFtxbh9N9RownYz26fAberutBrqxvcB84xsqPnc7YCZNWgBN",
    "memo_public_key": "GLS5A77uJ8GVH5VVzYVHs9jdhKd4TkMArqreenedsPwdncRFn3iCV",
    "testnet_id": 5,
    "user_db_id": 20117,
    "comment_id": 0
}*/
