package io.golos.cyber_android.ui.screens.profile_bio.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.screens.profile_bio.dto.PassResultCommand
import io.golos.cyber_android.ui.screens.profile_bio.dto.PrepareToCloseCommand
import io.golos.cyber_android.ui.screens.profile_bio.dto.TextLenInfo
import io.golos.cyber_android.ui.screens.profile_bio.model.ProfileBioModel
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Inject
import javax.inject.Named

class ProfileBioViewModel
@Inject
constructor(
    @Named(Clarification.TEXT)
    private val inputText: String?,
    dispatchersProvider: DispatchersProvider,
    model: ProfileBioModel
) : ViewModelBase<ProfileBioModel>(dispatchersProvider, model) {

    private val _postButtonEnabled = MutableLiveData<Boolean>(false)
    val postButtonEnabled: LiveData<Boolean> get() = _postButtonEnabled

    private val _textLenInfo = MutableLiveData<TextLenInfo>(TextLenInfo(0, model.maxTextLen))
    val texLenInfo: LiveData<TextLenInfo> get() = _textLenInfo

    private val _maxTextLen = MutableLiveData(model.maxTextLen)
    val maxTextLen: LiveData<Int> get() = _maxTextLen

    val text = MutableLiveData<String>(inputText ?: "")

    init {
        text.observeForever {
            val textLen = it.length

            _textLenInfo.value = TextLenInfo(textLen, model.maxTextLen)
            _postButtonEnabled.value = textLen > 0 && !it.isNullOrBlank()
        }
    }

    fun onCloseClick() {
        _command.value = PrepareToCloseCommand()
        _command.value = BackCommand()
    }

    fun onPostClick() {
        text.value
            ?.let {
                _command.value = PrepareToCloseCommand()
                _command.value = PassResultCommand(it)
                _command.value = BackCommand()
            }
    }
}
