package io.golos.cyber_android.application.dependency_injection

import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import io.golos.cyber_android.application.dependency_injection.scopes.ApplicationScope
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorAESOldApi
import io.golos.domain.Encryptor
import io.golos.domain.KeyValueStorageFacade
import io.golos.sharedmodel.Cyber4JConfig
import javax.inject.Named

/** Application level module - global objects are created here   */
@Module
class AppModule(private val appContext: Context) {
    private val cyber4jConfigs = mapOf(
        "stable" to Cyber4JConfig(
            blockChainHttpApiUrl = "http://116.202.4.39:8888/",
            servicesUrl = "ws://116.203.98.241:8080"
        ),
        "dev" to Cyber4JConfig(
            blockChainHttpApiUrl = "http://46.4.96.246:8888/",
            servicesUrl = "ws://159.69.33.136:8080"
        ),
        "unstable" to Cyber4JConfig(
            blockChainHttpApiUrl = "http://116.202.4.46:8888/",
            servicesUrl = "ws://159.69.33.136:8080"
        )
    )

    @Provides
    @ApplicationScope
    internal fun provideContext(): Context = appContext

    @Provides
    @ApplicationScope
    @Named(Clarification.AES)
    internal fun provideEncryptor(
        keyValueStorageFacade: KeyValueStorageFacade,
        @Named(Clarification.RSA) encryptor: Encryptor
    ): Encryptor {

        return if (Build.VERSION.SDK_INT >= 23) {
            EncryptorAES()
        } else {
            EncryptorAESOldApi(keyValueStorageFacade, encryptor)
        }
    }
}