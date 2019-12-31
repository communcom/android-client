package io.golos.cyber_android.application.dependency_injection.graph.app

import android.app.backup.BackupManager
import android.content.Context
import android.os.Build
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.golos.commun4j.sharedmodel.Commun4jConfig
import io.golos.cyber_android.BuildConfig
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import io.golos.cyber_android.core.encryption.aes.EncryptorAES
import io.golos.cyber_android.core.encryption.aes.EncryptorAESOldApi
import io.golos.cyber_android.core.logger.CrashlyticsTimberTreeDebug
import io.golos.cyber_android.core.logger.CrashlyticsTimberTreeRelease
import io.golos.cyber_android.core.logger.Cyber4JLogger
import io.golos.cyber_android.ui.screens.post_filters.PostFiltersHolder
import io.golos.cyber_android.ui.screens.login_sign_up.countries.CountriesRepository
import io.golos.cyber_android.ui.screens.login_sign_up.countries.CountriesRepositoryImpl
import io.golos.data.persistence.key_value_storage.KeyValueStorageFacade
import io.golos.domain.*
import io.golos.domain.dependency_injection.Clarification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Named

/** Application level module - global objects are created here   */
@Module
class AppModule(private val appContext: Context) {
    private val cyber4jConfigs = mapOf(
        "stable" to Commun4jConfig(
            blockChainHttpApiUrl = "http://116.202.4.39:8888/",
            servicesUrl = "wss://cyber-gate.golos.io"
        ),
        "dev" to Commun4jConfig(
            blockChainHttpApiUrl = "http://116.202.4.46:8888/",
            servicesUrl = "wss://dev-gate.commun.com"
        ),
        "unstable" to Commun4jConfig(
            blockChainHttpApiUrl = "http://116.202.4.46:8888/",
            servicesUrl = "wss://dev-gate.commun.com"
        ),
        "prod" to Commun4jConfig(
            blockChainHttpApiUrl = "http://116.203.212.190:8888/",
            servicesUrl = "wss://gate.golos.io"
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
    internal fun provideConfig(): Commun4jConfig =
        (cyber4jConfigs[BuildConfig.FLAVOR])!!
            .copy(
                httpLogger = Cyber4JLogger(Cyber4JLogger.HTTP),
                socketLogger = Cyber4JLogger(Cyber4JLogger.SOCKET)
            )

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
        deviceInfoProvider: DeviceInfoProvider
    ): CountriesRepository =
        CountriesRepositoryImpl(appContext, moshi, deviceInfoProvider)

    @Provides
    internal fun provideTimberTree(crashlytics: CrashlyticsFacade): Timber.Tree =
        if(BuildConfig.DEBUG) {
            CrashlyticsTimberTreeDebug()
        } else {
            CrashlyticsTimberTreeRelease(crashlytics)
        }

    @Provides
    @ApplicationScope
    internal fun providePostFilters(): PostFiltersHolder = PostFiltersHolder()
}