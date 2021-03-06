package io.golos.cyber_android.application.shared.crashlytics

import android.content.Context
import com.crashlytics.android.Crashlytics
import io.golos.cyber_android.BuildConfig
import io.golos.cyber_android.R
import io.golos.cyber_android.application.shared.display_info.DisplayInfoProvider
import io.golos.domain.CrashlyticsFacade
import io.golos.domain.DeviceInfoProvider
import javax.inject.Inject

class CrashlyticsFacadeImpl
@Inject
constructor(
    appContext: Context,
    deviceInfoProvider: DeviceInfoProvider
): CrashlyticsFacade {

    private val enabled = BuildConfig.CRASH_REPORTS_ENABLED

    init {
        doCall {
            Crashlytics.setString("BUILD_TYPE", BuildConfig.BUILD_TYPE)
            Crashlytics.setString("BUILD_FLAVOR", BuildConfig.FLAVOR)
            Crashlytics.setBool("IS_DEBUG_BUILD", BuildConfig.DEBUG)

            Crashlytics.setString("LOCALE", appContext.resources.getString(R.string.locale))

            Crashlytics.setString("COUNTRY", deviceInfoProvider.getCountryCode() ?: "")

            DisplayInfoProvider.getSizeInPix(appContext).apply {
                Crashlytics.setString("DISPLAY_SIZE_PIXELS", "${this.width}x${this.height} [pix]")
            }

            DisplayInfoProvider.getSizeInDp(appContext).apply {
                Crashlytics.setString("DISPLAY_SIZE_DP", "${this.width}x${this.height} [dp]")
            }

            DisplayInfoProvider.getDensityCategory(appContext).apply {
                Crashlytics.setString("DISPLAY_DENSITY_CATEGORY", this.toString())
            }

            DisplayInfoProvider.getSizeCategory(appContext).apply {
                Crashlytics.setString("DISPLAY_SIZE_CATEGORY", this.toString())
            }
        }
    }

    override fun registerUser(userName: String, userId: String) =
        doCall {
            Crashlytics.setString("USER_NAME", userName)
            Crashlytics.setString("USER_ID", userId)
        }

    override fun log(tag: String, string: String) =
        doCall {
            Crashlytics.log(0, tag, string)
        }

    override fun log(ex: Throwable) =
        doCall {
            val typeName = ex.javaClass.simpleName

            val callInfo = if(ex.stackTrace.isNotEmpty()) {
                ex.stackTrace[0].let { stackItem ->
                    stackItem.className.let {
                        "${it.substring(it.lastIndexOf(".") + 1, it.length)}::${stackItem.methodName} at ${stackItem.lineNumber}"
                    }
                }
            } else {
                "no stack trace data"
            }


            Crashlytics.log(0, "EXCEPTION", "Type: $typeName, message: ${ex.message} [$callInfo]")
        }

    override fun sendReport(text: String) =
        doCall {
            Crashlytics.log(0, "MESSAGE_FROM_USER", text)
            Crashlytics.logException(CrashlyticsReportException(text))
        }


    private fun doCall(call: () -> Unit) {
        if (enabled) {
            call()
        }
    }
}