package io.golos.cyber_android.ui.screens.profile_posts.view_commands

import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand

class NavigationToPostMenuViewCommand(val post: PostMenu) : ViewCommand

class SharePostCommand(val shareUrl: String) : ViewCommand

class EditPostCommand(val post: Post) : ViewCommand

class ReportPostCommand(val post: Post) : ViewCommand