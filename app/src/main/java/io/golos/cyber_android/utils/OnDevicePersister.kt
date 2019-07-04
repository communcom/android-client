package io.golos.cyber_android.utils

import android.content.Context
import android.util.Base64
import com.squareup.moshi.Moshi
import io.golos.domain.Logger
import io.golos.domain.Persister
import io.golos.domain.entities.AuthState
import io.golos.domain.requestmodel.PushNotificationsStateModel
import io.golos.sharedmodel.CyberName

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */

const val PREFS_NAME = "sp_2"

class OnDevicePersister(
    context: Context,
    private val logger: Logger
) : Persister {
    private val encryptor = EnCryptor(context)
    private val decryptor = DeCryptor()
    private val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().build()

    override fun savePushNotifsSettings(forUser: CyberName, settings: PushNotificationsStateModel) {
        editor.putString("pushes_of_${forUser.name}", moshi.adapter(PushNotificationsStateModel::class.java).toJson(settings)).apply()
    }

    override fun getPushNotifsSettings(forUser: CyberName): PushNotificationsStateModel {
        val authStateString = prefs.getString("pushes_of_${forUser.name}", null) ?: return PushNotificationsStateModel.DEFAULT
        return moshi.adapter(PushNotificationsStateModel::class.java).fromJson(authStateString)
            ?: PushNotificationsStateModel.DEFAULT
    }

    override fun saveAuthState(state: AuthState) {
        editor.putString("auth_state", moshi.adapter(AuthState::class.java).toJson(state)).apply()
    }

    override fun getAuthState(): AuthState? {

        val authStateString = prefs.getString("auth_state", null) ?: return null

        return moshi.adapter(AuthState::class.java).fromJson(authStateString)
    }

    override fun saveActiveKey(activeKey: String) {
        try {
            val resultByteArray = encryptor.encryptText(KeystoreKeyAlias.ACTIVE_WIF, activeKey)
            val base64String = Base64.encodeToString(resultByteArray, Base64.DEFAULT)
            editor.putString("bb", base64String).apply()
        } catch (e: java.lang.Exception) {
            logger(e)
        }
    }

    override fun getActiveKey(): String? {
        try {
            val base64 = prefs.getString("bb", null) ?: return null
            val byteArray = Base64.decode(base64, Base64.DEFAULT)
            return decryptor.decryptData(KeystoreKeyAlias.ACTIVE_WIF, byteArray)
        } catch (e: Exception) {
            logger(e)
            return null
        }
    }
}