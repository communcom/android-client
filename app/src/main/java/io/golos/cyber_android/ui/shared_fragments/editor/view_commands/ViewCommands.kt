package io.golos.cyber_android.ui.shared_fragments.editor.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkType
import io.golos.domain.post_editor.LinkInfo

class InsertExternalLinkViewCommand(val linkInfo: ExternalLinkInfo): ViewCommand

class UpdateLinkInTextViewCommand(
    val isEdit: Boolean,
    val linkInfo: LinkInfo
) : ViewCommand