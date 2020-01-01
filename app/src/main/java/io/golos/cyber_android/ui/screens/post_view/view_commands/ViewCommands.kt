package io.golos.cyber_android.ui.screens.post_view.view_commands

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.domain.use_cases.model.DiscussionIdModel

@Deprecated("")
class StartEditPostViewCommand(val postId: ContentId) : ViewCommand

class NavigationToEditPostViewCommand(val contentId: ContentId) : ViewCommand

class NavigationToPostMenuViewCommand(val postMenu: PostMenu) : ViewCommand

class SharePostCommand(val shareUrl: String) : ViewCommand

class ReportPostCommand(val contentId: ContentId) : ViewCommand

class DeletePostCommand : ViewCommand

class ShowCommentsSortingMenuViewCommand : ViewCommand

class ClearCommentTextViewCommand : ViewCommand

class ShowCommentMenuViewCommand(val commentId: DiscussionIdModel) : ViewCommand