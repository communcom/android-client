package io.golos.cyber_android.ui.shared_fragments.editor.view_commands

import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared_fragments.editor.dto.ExternalLinkInfo
import io.golos.domain.interactors.model.DiscussionCreationResultModel
import io.golos.domain.post.editor_output.LinkInfo

class InsertExternalLinkViewCommand(val linkInfo: ExternalLinkInfo): ViewCommand

class UpdateLinkInTextViewCommand(
    val isEdit: Boolean,
    val linkInfo: LinkInfo
) : ViewCommand

class PostCreatedViewCommand(val result: DiscussionCreationResultModel): ViewCommand

class PostErrorViewCommand(val result: Throwable): ViewCommand