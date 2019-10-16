package io.golos.cyber_android.ui.shared_fragments.post.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand

class NavigateToImageViewCommand(val imageUri: Uri): ViewCommand

class NavigateToLinkViewCommand(val link: Uri): ViewCommand

class NavigateToUserProfileViewCommand(val userId: String): ViewCommand