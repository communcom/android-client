package io.golos.cyber_android.ui.screens.post_edit.fragment.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.post_edit.fragment.dto.ExternalLinkInfo
import io.golos.domain.dto.ContentIdDomain

class InsertExternalLinkViewCommand(val linkInfo: ExternalLinkInfo) : ViewCommand

class PostCreatedViewCommand(val contentId: ContentIdDomain) : ViewCommand

class PastedLinkIsValidViewCommand(val uri: Uri) : ViewCommand