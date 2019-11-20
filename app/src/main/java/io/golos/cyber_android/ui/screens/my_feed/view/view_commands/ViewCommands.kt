package io.golos.cyber_android.ui.screens.my_feed.view.view_commands

import android.net.Uri
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.post_page_menu.model.PostMenu
import io.golos.domain.use_cases.model.DiscussionIdModel

class NavigateToImageViewCommand(val imageUri: Uri) : ViewCommand

class NavigateToLinkViewCommand(val link: Uri) : ViewCommand

class NavigateToUserProfileViewCommand(val userId: String) : ViewCommand

class NavigationToPostMenuViewCommand(val post: PostMenu) : ViewCommand

class NavigateToPostCommand(val discussionIdModel: DiscussionIdModel) : ViewCommand

class SharePostCommand(val shareUrl: String) : ViewCommand

class EditPostCommand(val post: Post) : ViewCommand

class ReportPostCommand(val post: Post) : ViewCommand