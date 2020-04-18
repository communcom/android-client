package io.golos.cyber_android.ui.screens.post_edit.fragment.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkInfo
import io.golos.domain.use_cases.post.editor_output.LinkInfo

class InsertExternalLinkViewCommand(val linkInfo: ExternalLinkInfo) : ViewCommand

class PostCreatedViewCommand(val contentId: ContentId) : ViewCommand

class PastedLinkIsValidViewCommand(val uri: Uri) : ViewCommand