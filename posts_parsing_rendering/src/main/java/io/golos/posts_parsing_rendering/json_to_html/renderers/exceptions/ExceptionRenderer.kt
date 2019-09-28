package io.golos.posts_parsing_rendering.json_to_html.renderers.exceptions

import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.domain.AppResourcesProvider
import io.golos.posts_parsing_rendering.R
import io.golos.posts_parsing_rendering.json_to_dto.IncompatibleVersionsException

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