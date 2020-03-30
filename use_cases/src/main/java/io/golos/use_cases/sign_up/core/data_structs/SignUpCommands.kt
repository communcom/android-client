package io.golos.use_cases.sign_up.core.data_structs

interface SignUpCommand

class StartGoogleSignIn: SignUpCommand
class StartFbSignIn: SignUpCommand

class ShowLoading: SignUpCommand
class HideLoading: SignUpCommand

class NavigateToPhone: SignUpCommand
class NavigateToPhoneVerification: SignUpCommand
class NavigateToSelectMethod: SignUpCommand
class NavigateToWelcome: SignUpCommand
class NavigateToUserName: SignUpCommand
class NavigateToGetPassword: SignUpCommand
class NavigateToConfirmPassword: SignUpCommand
class NavigateToPinCode: SignUpCommand
class NavigateToSelectUnlock: SignUpCommand

class SingUpCompleted: SignUpCommand

data class ShowError(val errorCode: SignUpMessageCode): SignUpCommand
data class ShowMessage(val messageCode: SignUpMessageCode): SignUpCommand

class PhoneVerificationCodeResendCompleted: SignUpCommand
