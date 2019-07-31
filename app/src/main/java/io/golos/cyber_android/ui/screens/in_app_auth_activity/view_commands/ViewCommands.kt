package io.golos.cyber_android.ui.screens.in_app_auth_activity.view_commands

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.widgets.pin.Digit

class AuthSuccessCommand: ViewCommand

class SwitchToPinCodeCommand: ViewCommand

class SwitchToFingerprintCommand: ViewCommand

class SetPinCodeDigitCommand(val digit: Digit): ViewCommand

class ResetPinCommand(): ViewCommand