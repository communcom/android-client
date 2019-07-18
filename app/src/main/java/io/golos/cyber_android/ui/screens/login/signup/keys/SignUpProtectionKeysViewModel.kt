package io.golos.cyber_android.ui.screens.login.signup.keys

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import io.golos.domain.interactors.model.GeneratedUserKeys
import io.golos.domain.map

class SignUpProtectionKeysViewModel : ViewModel() {

    private val keysLiveData = MutableLiveData<GeneratedUserKeys>()

    val getKeysLiveData = keysLiveData.map {
        it ?: return@map emptyList<UserKey>()
        listOf(
            UserKey(UserKeyType.MASTER, it.masterPassword),
            UserKey(UserKeyType.POSTING, it.postingPrivateKey),
            UserKey(UserKeyType.ACTIVE, it.activePrivateKey),
            UserKey(UserKeyType.MEMO, it.memoPrivateKey)
        )
    }

    fun setInitialKeys(keys: GeneratedUserKeys) {
        keysLiveData.postValue(keys)
    }
}
