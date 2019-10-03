package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.widgets.*
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.post.post_dto.*
import io.golos.posts_parsing_rendering.mappers.json_to_dto.JsonMappingErrorCode
import io.golos.posts_parsing_rendering.mappers.json_to_dto.JsonToDtoMapper
import kotlinx.android.synthetic.main.item_content_text.view.*
import kotlinx.coroutines.*
import java.lang.UnsupportedOperationException
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

    @Inject
    internal lateinit var logger: Logger

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)
    }

    fun bind(text: String, recyclerView: RecyclerView) {
        view.loadingIndicator.visibility = View.VISIBLE
        view.postWidgetContainer.visibility = View.INVISIBLE
        view.errorHolder.visibility = View.INVISIBLE

        this.recyclerView = recyclerView

        renderJob = launch {
            try {
                val post = withContext(dispatchersProvider.calculationsDispatcher) {
                    JsonToDtoMapper(App.logger).map(text)
                }

                when(post) {
                    is Either.Failure -> {
                        when(post.value) {
                            JsonMappingErrorCode.GENERAL -> showError(R.string.common_general_error)
                            JsonMappingErrorCode.JSON -> showError(R.string.invalid_post_format)
                            JsonMappingErrorCode.INCOMPATIBLE_VERSIONS -> showError(R.string.post_processor_is_too_format)
                        }
                    }

                    is Either.Success -> {
                        view.postWidgetContainer.visibility = View.VISIBLE

                        view.postWidgetContainer.removeAllViews()
                        post.value.content.forEach { block ->
                            view.postWidgetContainer.addView(createWidget(block) as View)
                        }

                        post.value.attachments?.let { view.postWidgetContainer.addView(createWidget(it) as View) }
                    }
                }
            } catch (ex: Exception) {
                logger.log(ex)
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