package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.PostTextRenderingFacade
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.model.TextRowModel
import kotlinx.android.synthetic.main.item_content_text.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PostTextViewHolder(val view: View) : RecyclerView.ViewHolder(view), CoroutineScope {

    private var scopeJob: Job = SupervisorJob()

    private var renderJob: Job? = null


    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    @Inject
    internal lateinit var dispatchersProvider: DispatchersProvider

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)
    }

    fun bind(textRowModel: TextRowModel) {
        renderJob = launch {
            val html = PostTextRenderingFacade(dispatchersProvider, appResourcesProvider).render(textRowModel.text.toString())
            view.webView.loadHtml(html)
        }
    }

    fun cleanUp() {
        renderJob?.takeIf { it.isActive }?.cancel()
    }
}