package io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.shared_fragments.post.view.view_holders.post_text.widgets.*
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.post.post_dto.*
import kotlinx.android.synthetic.main.item_content_text.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class PostTextViewHolder(val view: View) : RecyclerView.ViewHolder(view), CoroutineScope, LifecycleObserver {

    private var scopeJob: Job = SupervisorJob()

    private var renderJob: Job? = null

    private lateinit var recyclerView: RecyclerView

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    @Inject
    internal lateinit var dispatchersProvider: DispatchersProvider

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)
    }

    fun bind(post: PostBlock, recyclerView: RecyclerView) {
        view.loadingIndicator.visibility = View.VISIBLE
        view.postWidgetContainer.visibility = View.INVISIBLE
        view.errorHolder.visibility = View.INVISIBLE

        this.recyclerView = recyclerView

        renderJob = launch {
            try {
                view.postWidgetContainer.visibility = View.VISIBLE

                view.postWidgetContainer.removeAllViews()
                post.content.forEach { block ->
                    view.postWidgetContainer.addView(createWidget(block) as View)
                }

                post.attachments?.let { view.postWidgetContainer.addView(createWidget(it) as View) }
            } catch (ex: Exception) {
                Timber.e(ex)
                showError(R.string.common_general_error)
            } finally {
                view.loadingIndicator.visibility = View.INVISIBLE
            }
        }
    }

    fun cleanUp() {
        renderJob?.takeIf { it.isActive }?.cancel()

        with(view.postWidgetContainer) {
            for(i in 0 until childCount) {
                getChildAt(i).let {it as? PostBlockWidget<*>}?.cancel()
            }
        }
    }

    private fun showError(@StringRes errorText: Int) {
        view.errorHolder.text = appResourcesProvider.getString(errorText)
        view.errorHolder.visibility = View.VISIBLE
    }

    private fun createWidget(block: Block): PostBlockWidget<*> =
        when(block) {
            is AttachmentsBlock -> AttachmentsWidget(this.view.context).apply { render(block) }
            is ImageBlock -> EmbedImageWidget(this.view.context).apply { render(block) }
            is VideoBlock -> EmbedVideoWidget(this.view.context).apply { render(block) }
            is WebsiteBlock -> EmbedWebsiteWidget(this.view.context).apply { render(block) }
            is ParagraphBlock -> ParagraphWidget(this.view.context).apply { render(block) }
            else -> throw UnsupportedOperationException("This type of block is not supported: $block")
        }
}