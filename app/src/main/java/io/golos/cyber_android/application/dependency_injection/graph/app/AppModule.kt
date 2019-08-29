package io.golos.cyber_android.application.dependency_injection.graph.app

import android.app.backup.BackupManager
import android.content.Context
import android.os.Build
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.golos.cyber4j.Cyber4J
import io.golos.cyber_android.BuildConfig
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorAESOldApi
import io.golos.cyber_android.core.logger.Cyber4JLogger
import io.golos.data.repositories.countries.CountriesRepository
import io.golos.data.repositories.countries.CountriesRepositoryImpl
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import io.golos.sharedmodel.Cyber4JConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
        ),
        "prod" to Cyber4JConfig(
            blockChainHttpApiUrl = "http://116.203.212.190:8888/",
            servicesUrl = "ws://116.203.208.111:8080"
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

    @Provides
    @ApplicationScope
    internal fun provideCyber4J(logger: Logger): Cyber4J =
        (cyber4jConfigs[BuildConfig.FLAVOR] ?: Cyber4JConfig())
            .copy(
                httpLogger = Cyber4JLogger(logger, Cyber4JLogger.HTTP),
                socketLogger = Cyber4JLogger(logger, Cyber4JLogger.SOCKET)
            )
            .let { Cyber4J(it) }

    @Provides
    internal fun provideDispatchersProvider(): DispatchersProvider = object : DispatchersProvider {
        override val uiDispatcher: CoroutineDispatcher
            get() = Dispatchers.Main
        override val calculationsDispatcher: CoroutineDispatcher
            get() = Dispatchers.Default
        override val ioDispatcher: CoroutineDispatcher
            get() = Dispatchers.IO
    }

    @Provides
    internal fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    internal fun provideBackupManager(appContext: Context): BackupManager = BackupManager(appContext)

    @Provides
    internal fun provideCountriesRepository(
        moshi: Moshi,
        appResourcesProvider: AppResourcesProvider,
        deviceInfoProvider: DeviceInfoProvider
    ): CountriesRepository = CountriesRepositoryImpl(appResourcesProvider, moshi, deviceInfoProvider)
}