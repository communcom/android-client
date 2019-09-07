package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text

import android.annotation.SuppressLint
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.RecyclerView
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.post_page_fragment.PostPageFragmentComponent
import io.golos.cyber_android.core.display_info.DisplayInfoProvider
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
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri


class PostTextViewHolder(val view: View) : RecyclerView.ViewHolder(view), CoroutineScope {

    private var scopeJob: Job = SupervisorJob()

    private var renderJob: Job? = null

    private lateinit var recyclerView: RecyclerView
    private var webViewHeight = 0

    init {
        setupWebView()
    }

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    @Inject
    internal lateinit var dispatchersProvider: DispatchersProvider

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    @Inject
    internal lateinit var displayInfoProvider: DisplayInfoProvider

    init {
        App.injections.get<PostPageFragmentComponent>().inject(this)
    }

    fun bind(textRowModel: TextRowModel, recyclerView: RecyclerView) {
        view.loadingIndicator.visibility = View.VISIBLE
        view.webView.visibility = View.INVISIBLE

        this.recyclerView = recyclerView

        renderJob = launch {
            val html = PostTextRenderingFacade(dispatchersProvider, appResourcesProvider).render(textRowModel.text.toString())

            view.loadingIndicator.visibility = View.INVISIBLE
            view.webView.visibility = View.VISIBLE

            view.webView.loadHtml(html)
        }
    }

    fun cleanUp() {
        renderJob?.takeIf { it.isActive }?.cancel()
    }

    private fun setupWebView() {
        with(view.webView) {
            isFocusable = false
            isFocusableInTouchMode = false

            @SuppressLint("SetJavaScriptEnabled")
            settings.javaScriptEnabled = true

            addJavascriptInterface(object {
                @Suppress("unused")
                @JavascriptInterface
                /**
                 * We need to scroll to the block
                 * [blockTopOffset] top offset of the block in pixels
                 */
                fun onScrollToBlock(blockTopOffset: Int, viewportHeight: Int) {
                    val topOffsetRecalculated = ((webViewHeight/viewportHeight.toFloat())*blockTopOffset).toInt()

                    val halfScreenSize = displayInfoProvider.sizeInPix.height/2
                    recyclerView.smoothScrollBy(0, -(webViewHeight-topOffsetRecalculated-halfScreenSize))
                    return
                }
            }, "Android")

            webViewClient = object : WebViewClient() {
                /**
                 * Opens links in an external application
                 */
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                        view!!.context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        )
                        true
                    } else {
                        false
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    webViewHeight = view!!.height
                }
            }
        }
    }

}