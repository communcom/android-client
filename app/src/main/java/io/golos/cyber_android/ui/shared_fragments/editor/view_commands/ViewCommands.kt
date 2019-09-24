package io.golos.cyber_android.ui.shared_fragments.editor.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkType

class InsertExternalLinkViewCommand(val linkInfo: ExternalLinkInfo): ViewCommand

class UpdateLinkInTextViewCommand(
    val isEdit: Boolean,
    val text: String,
    val uri: Uri,
    val type: ExternalLinkType
) : ViewCommand