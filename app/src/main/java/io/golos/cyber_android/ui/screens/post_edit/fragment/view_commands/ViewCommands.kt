package io.golos.cyber_android.ui.screens.post_edit.fragment.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkInfo

class InsertExternalLinkViewCommand(val linkInfo: ExternalLinkInfo) : ViewCommand

class PostCreatedViewCommand(val contentId: ContentId) : ViewCommand

class PastedLinkIsValidViewCommand(val uri: Uri) : ViewCommand