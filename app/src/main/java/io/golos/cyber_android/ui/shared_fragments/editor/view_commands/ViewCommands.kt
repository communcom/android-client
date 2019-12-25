package io.golos.cyber_android.ui.shared_fragments.editor.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.domain.use_cases.post.editor_output.LinkInfo

class InsertExternalLinkViewCommand(val linkInfo: ExternalLinkInfo) : ViewCommand

class UpdateLinkInTextViewCommand(
    val isEdit: Boolean,
    val linkInfo: LinkInfo
) : ViewCommand

class PostCreatedViewCommand(val contentId: ContentId) : ViewCommand

class PostErrorViewCommand(val result: Throwable) : ViewCommand

class PastedLinkIsValidViewCommand(val uri: Uri) : ViewCommand