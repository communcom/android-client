package io.golos.domain

import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import io.golos.domain.interactors.model.GeneratedUserKeys

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
}