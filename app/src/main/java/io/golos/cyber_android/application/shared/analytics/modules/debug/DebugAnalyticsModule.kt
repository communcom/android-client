package io.golos.cyber_android.application.shared.analytics.modules.debug

import android.app.Application
import io.golos.cyber_android.application.shared.analytics.dto.PasswordBackup
import io.golos.cyber_android.application.shared.analytics.dto.RegistrationSelection
import io.golos.cyber_android.application.shared.analytics.dto.SmsCodeEntered
import io.golos.cyber_android.application.shared.analytics.dto.UsernameEntered
import io.golos.cyber_android.application.shared.analytics.modules.AnalyticsModule
import timber.log.Timber
import java.lang.StringBuilder

class DebugAnalyticsModule: AnalyticsModule {
    override fun init(app: Application) { }

    /**
     * Event: Click get started 0.3.1
     */
    override fun clickGetStarted031() = sendEvent("Click get started 0.3.1")

    /**
     * Event: Open screen 0.1.1
     */
    override fun openScreen011() = sendEvent("Open screen 0.1.1")

    /**
     * Event: Open screen 0.1.2
     */
    override fun openScreen012() = sendEvent("Open screen 0.1.2")

    /**
     * Event: Open screen 0.1.3
     */
    override fun openScreen013() = sendEvent("Open screen 0.1.3")

    /**
     * Event: Click log in 0.1
     */
    override fun clickLogIn01() = sendEvent("Click log in 0.1")
    /**
     * Event: Registration Selection
     */
    override fun registrationSelection(param: RegistrationSelection) =
        sendEvent("Registration Selection") {
            when(param) {
                RegistrationSelection.PHONE -> 1
                RegistrationSelection.GOOGLE -> 2
                RegistrationSelection.FACEBOOK -> 3
                RegistrationSelection.APPLE -> 4
            }.let { append("answer: ", it) }
        }

    /**
     * Event: Country selected
     * [available] true - the country is accessible; false - a user tried to select disabled country
     * [code] phone code
     */
    override fun countrySelected(available: Boolean, code: String) =
        sendEvent("Country selected") {
            append("available", available)
            append("code", code)
        }

    /**
     * Event: Phone number entered
     */
    override fun phoneNumberEntered() = sendEvent("Phone number entered")

    /**
     * Event: Sms code entered
     */
    override fun smsCodeEntered(param: SmsCodeEntered) =
        sendEvent("Sms code entered") {
            when(param) {
                SmsCodeEntered.RIGHT -> "right"
                SmsCodeEntered.ERROR -> "error"
                SmsCodeEntered.RESEND -> "resend"
            }.let { append("answer: ", it) }
        }

    /**
     * Event: Username entered
     */
    override fun usernameEntered(available: UsernameEntered) =
        sendEvent("Username entered") {
            when(available) {
                UsernameEntered.SUCCESS -> "success"
                UsernameEntered.ERROR -> "error"
                UsernameEntered.HELP -> "help"
            }.let { append("answer: ", it) }
        }

    /**
     * Event: Password copy
     */
    override fun passwordCopy() = sendEvent("Password copy")

    /**
     * Event: Password backuped
     */
    override fun passwordBackuped(param: PasswordBackup) =
        sendEvent("Password backuped") {
            when(param) {
                PasswordBackup.PDF -> "PDF"
                PasswordBackup.CLOUD -> "Backup"
            }.let { append("answer: ", it) }
        }

    /**
     * Event: Password not backuped
     * [param] true - an extra notification about saving a password has been skipped;
     * false - the user has returned to saving a password
     */
    override fun passwordNotBackuped(param: Boolean) = sendEvent("Password not backuped") { append("answer: ", param) }

    /**
     * Event: FaceID/TouchID activated
     * [param] true - was activated; false - wasn't activated
     */
    override fun faceIDTouchIDActivated(param: Boolean) = sendEvent("FaceID/TouchID activated") { append("answer: ", param) }

    /**
     * Event: Open screen 1.
     */
    override fun openScreen1() = sendEvent("Open screen 1.")

    /**
     * Event: Open screen 1.1.0
     */
    override fun openScreen110() = sendEvent("Open screen 1.1.0")

    /**
     * Event: Open screen 1.1.1
     */
    override fun openScreen111() = sendEvent("Open screen 1.1.1")

    /**
     * Event: Open screen 1.1.2
     */
    override fun openScreen112() = sendEvent("Open screen 1.1.2")

    /**
     * Event: Open screen 1.1.3
     */
    override fun openScreen113() = sendEvent("Open screen 1.1.3")

    /**
     * Event: Open screen 1.1.4
     */
    override fun openScreen114() = sendEvent("Open screen 1.1.4")

    /**
     * Event: Open screen 1.1.5
     */
    override fun openScreen115() = sendEvent("Open screen 1.1.5")

    /**
     * Event: Open screen 1.1.6
     */
    override fun openScreen116() = sendEvent("Open screen 1.1.6")

    /**
     * Event: Google auth
     * [param] true - access to Google has been granted; false - otherwise
     */
    override fun googleAuth(param: Boolean) = sendEvent("Google auth") { append("answer: ", param) }

    /**
     * Event: Facebook auth
     * [param] true - access to Facebook has been granted; false - otherwise
     */
    override fun facebookAuth(param: Boolean) = sendEvent("Facebook auth") { append("answer: ", param) }

    /**
     * Event: Apple auth
     * [param] true - access to the Apple has been granted; false - otherwise
     */
    override fun appleAuth(param: Boolean) = sendEvent("Apple auth") { append("answer: ", param) }

    /**
     * Event: Open screen 1.2.1
     */
    override fun openScreen121() = sendEvent("Open screen 1.2.1")

    /**
     * Event: Open screen 1.3.1
     */
    override fun openScreen131() = sendEvent("Open screen 1.3.1")

    /**
     * Event: Open screen 1.4.1
     */
    override fun openScreen141() = sendEvent("Open screen 1.4.1")

    /**
     * Event: Bounty subscribe
     * [num] subscriptions quantity
     */
    override fun bountySubscribe(num: Int) = sendEvent("Bounty subscribe") { append("num", num) }

    /**
     * Event: Share activated
     * [param] true - if a user shared a link; false - otherwise
     */
    override fun shareActivated(param: Boolean) = sendEvent("Share activated") { append("answer: ", param) }

    /**
     * Event: app was download
     * [param] true - the app has been downloaded; false - otherwise
     */
    override fun appWasDownloaded(param: Boolean) = sendEvent("app was download") { append("answer: ", param) }

    /**
     * Event: Share activated referal
     * [param] true - if a user shared a link; false - otherwise
     */
    override fun shareActivatedReferal(param: Boolean) = sendEvent("Share activated referal") { append("answer: ", param) }

    private fun sendEvent(eventText: String) = Timber.tag("ANALYTIC_EVENT").d(eventText)

    private fun sendEvent(eventText: String, paramsAction: StringBuilder.() -> Unit) =
        Timber.tag("ANALYTIC_EVENT").d("$eventText ${StringBuilder().also(paramsAction)}")

}