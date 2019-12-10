package io.golos.domain

import io.golos.domain.dto.UserKey
import io.golos.domain.dto.UserKeyType
import io.golos.domain.use_cases.model.GeneratedUserKeys

interface UserKeyStore {
    /**
     * Generates new keys, stores and returns them
     */
    fun createKeys(userId: String, userName: String): GeneratedUserKeys

    /**
     * Generates new keys, stores and returns them
     */
    fun createKeys(userId: String, userName: String, masterKey: String): GeneratedUserKeys

    /**
     * Returns private part of a key (in case of master - key itself)
     */
    fun getKey(keyType: UserKeyType): String

    /**
     * Updates keys in a storage
     */
    fun updateKeys(keys: List<UserKey>)

    /**
     * Removes all keys
     */
    fun clearAllKeys()
}