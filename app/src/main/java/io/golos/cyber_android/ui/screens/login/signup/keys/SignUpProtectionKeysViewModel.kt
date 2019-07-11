package io.golos.cyber_android.ui.screens.login.signup.keys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.interactors.model.GeneratedUserKeys
import io.golos.domain.map

data class Key(val keyType: KeyType, val key: String)

enum class KeyType {
    MASTER, POSTING, ACTIVE, MEMO
}

class SignUpProtectionKeysViewModel : ViewModel() {

    private val keysLiveData = MutableLiveData<GeneratedUserKeys>()

    val getKeysLiveData = keysLiveData.map {
        it ?: return@map emptyList<Key>()
        listOf(
            Key(KeyType.MASTER, it.masterPassword),
            Key(KeyType.POSTING, it.postingPrivateKey),
            Key(KeyType.ACTIVE, it.activePrivateKey),
            Key(KeyType.MEMO, it.memoPrivateKey)
        )
    }

    init {
        keysLiveData.postValue(
            GeneratedUserKeys(
                "name", "master", "owner",
                "ownerP", "active", "activeP",
                "posting", "postingP",
                "memo", "memoP"
            )
        )
    }

    fun setInitialKeys(keys: GeneratedUserKeys) {
        keysLiveData.postValue(keys)
    }


}
