package io.golos.cyber_android.ui.screens.profile_posts.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.domain.use_cases.model.DiscussionIdModel

class NavigationToPostMenuViewCommand(val post: PostMenu) : ViewCommand

class NavigateToPostCommand(
    val discussionIdModel: DiscussionIdModel,
    val contentId: ContentId
) : ViewCommand

class SharePostCommand(val shareUrl: String) : ViewCommand

class EditPostCommand(val post: Post) : ViewCommand

class ReportPostCommand(val post: Post) : ViewCommand