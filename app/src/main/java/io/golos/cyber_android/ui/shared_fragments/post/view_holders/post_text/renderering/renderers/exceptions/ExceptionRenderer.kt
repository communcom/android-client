package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.exceptions

import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.domain.AppResourcesProvider

class ExceptionRenderer(
    private val appResources: AppResourcesProvider,
    private val builder: HtmlBuilder
) {
    fun render(ex: Exception) {
        builder.clear()

        builder.putDoctype()
        builder.putHtml {
            builder.putStyles()

            builder.putBody {
                builder.putHeader(
                    when(ex) {
                        is IncompatibleVersionsException -> appResources.getString(R.string.post_rendering_low_version_error)
                        else -> appResources.getString(R.string.post_rendering_general_error)
                    }
                , 3)
            }
        }
    }
}