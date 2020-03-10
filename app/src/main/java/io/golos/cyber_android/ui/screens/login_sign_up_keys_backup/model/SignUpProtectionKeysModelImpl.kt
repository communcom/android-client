package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model

import android.util.Base64
import com.squareup.moshi.Moshi
import io.golos.cyber_android.ui.screens.login_activity.shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.PdfPageExportData
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.QrCodeData
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model.page_renderer.PageRenderer
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.UserKey
import io.golos.domain.dto.UserKeyType
import io.golos.domain.repositories.CurrentUserRepositoryRead
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

class SignUpProtectionKeysModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade,
    private val currentUserRepository: CurrentUserRepositoryRead,
    private val dataPass: LoginActivityFragmentsDataPass,
    private val moshi: Moshi,
    override val pageRenderer: PageRenderer
) : ModelBaseImpl(), SignUpProtectionKeysModel {

    private lateinit var _allKeys: List<UserKey>
    override val allKeys: List<UserKey>
        get() = _allKeys

    override suspend fun loadKeys() {
        _allKeys = withContext(dispatchersProvider.ioDispatcher) {
            listOf(
                UserKeyType.MASTER,
                UserKeyType.OWNER,
                UserKeyType.ACTIVE,
                UserKeyType.POSTING,
                UserKeyType.MEMO
            )
                .map { keyType ->
                    UserKey(keyType, userKeyStore.getKey(keyType))
                }
        }
    }

    override suspend fun saveKeysExported() {
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val newAuthState = keyValueStorage.getAuthState()!!.copy(isKeysExported = true)
                keyValueStorage.saveAuthState(newAuthState)
            } catch(ex: Exception) {
                Timber.e(ex)
                throw ex
            }
        }
    }

    override fun getDataForExporting(): PdfPageExportData {
        val userId = currentUserRepository.userId.userId
        val userName = currentUserRepository.userName
        val password = allKeys.single { it.keyType == UserKeyType.MASTER }.key

        @Suppress("SimpleRedundantLet")
        val qrCode = QrCodeData(userId, userName, password)
            .let { moshi.adapter(QrCodeData::class.java).toJson(it) }
            .let { it.toByteArray(Charsets.UTF_8) }
            .let { Base64.encodeToString(it, Base64.DEFAULT) }


        return  PdfPageExportData(
            userName = userName,
            userId = userId,
            createDate = Date(),
            phoneNumber = dataPass.getPhonePhone() ?: "",
            password = password,
            activeKey = allKeys.single { it.keyType == UserKeyType.ACTIVE }.key,
            ownerKey = allKeys.single { it.keyType == UserKeyType.OWNER }.key,
            qrText = qrCode
        )
    }

    override suspend fun copyExportedDocumentTo(exportPath: String): File =
        withContext(dispatchersProvider.ioDispatcher) {
            val target = File(exportPath, pageRenderer.document!!.name)
            pageRenderer.document!!.copyTo(target, true)
            target
        }
}