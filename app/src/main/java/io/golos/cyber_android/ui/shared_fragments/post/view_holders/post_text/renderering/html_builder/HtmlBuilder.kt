package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder

@Suppress("SpellCheckingInspection")
class HtmlBuilder {
    private var isFirstBlockInPostMap = true

    private val output = StringBuilder()

    fun build(): String = output.toString()

    fun clear() = output.clear()

    fun putDoctype() = putTag("!DOCTYPE html")

    fun putHtml(innerAction: () -> Unit) = putSection("html", innerAction)

    fun putStyles() =
        putSection("style") {
            putString(".text_center { text-align: center; }")
            putString(".border { border-style: solid; border-color: #bbbbbb; border-width: 1pt; }")
            putString(".image_description { font-style: italic; }")
            putString(".carousel_height { height: 120pt; }")
            putString(".carousel { width: 100%; overflow: auto; white-space:nowrap }")
            putString(".carousel_padding { padding-left: 2pt; }")
            putString(".carousel_block { display: inline-block; }")
            putString(".block_wrapper { margin-bottom: 12pt; }")
        }

    fun putBody(innerAction: () -> Unit) = putSection("body", innerAction)

    fun putParagraph(innerAction: () -> Unit) = putSection("p", innerAction)

    fun putString(s: String) = append(s)

    fun putHeader(text: String, level: Int) {
        level
            .let {
                when {
                    it < 1 -> 1
                    it > 6 -> 6
                    else -> it
                }
            }
            .also {
                putSection("h$it", "text_center") {
                    putString(text)
                }
            }
    }

    fun putBoldIf(condition: () -> Boolean, innerAction: () -> Unit) = putSectionWithCondition("b", condition, innerAction)

    fun putItalicIf(condition: () -> Boolean, innerAction: () -> Unit) = putSectionWithCondition("i", condition, innerAction)

    fun putTextColorIf(color: String, condition: () -> Boolean, innerAction: () -> Unit) {
        if(condition()) {
            append("<span style=\"color:")
            append(color)
            append("\">")
        }

        innerAction()

        if(condition()) {
            putTag("span", false)
        }
    }

    fun putLink(linkText: String, url: String) {
        append("<a href=\"")
        append(url)
        append("\">")

        append(linkText)

        putTag("a", false)
    }

    fun putFigure(url: String, innerAction: () -> Unit) {
        putSection("figure") {
            putImage(url, "border", "100%")
            innerAction()
        }
    }

    fun putFigureCaption(text: String) =
        putSection("figcaption", "image_description text_center") {
            append(text)
        }

    fun putPostMap(innerAction: () -> Unit) {
        isFirstBlockInPostMap = true
        putSection("div", "carousel carousel_height") {
            innerAction()
        }
    }

    fun putPostMapBlock(imageUrl: String) {
        val cssClass = if(isFirstBlockInPostMap) {
            isFirstBlockInPostMap = false
            "carousel_block"
        } else {
            "carousel_block carousel_padding"
        }

        putSection("div", cssClass) {
            putImage(imageUrl, "carousel_height", null)
        }
    }

    fun putImage(url: String, cssClass: String, width: String?) {
        append("<img class=\"")
        append(cssClass)
        append("\" src=\"")
        append(url)
        append("\"")

        if(width != null) {
            append(" width = \"")
            append(width)
            append("\"")
        }

        append(">")
    }

    fun putBlockWrapper(innerAction: () -> Unit) =
        putSection("div", "block_wrapper") {
            innerAction()
        }

    private fun putSection(sectionTag: String, innerAction: () -> Unit) {
        putTag(sectionTag)
        innerAction()
        putTag(sectionTag, false)
    }

    private fun putSection(sectionTag: String, cssClass: String,  innerAction: () -> Unit) {
        append("<")
        append(sectionTag)
        append(" class = \"")
        append(cssClass)
        append("\">")

        innerAction()
        putTag(sectionTag, false)
    }

    private fun putSectionWithCondition(sectionTag: String, condition: () -> Boolean, innerAction: () -> Unit) {
        if(condition()) {
            putTag(sectionTag)
        }

        innerAction()

        if(condition()) {
            putTag(sectionTag, false)
        }
    }

    private fun putTag(tag: String, isOpen: Boolean = true) {
        append("<")
        if(!isOpen) {
            append("/")
        }
        append(tag)
        append(">")
    }

    private fun append(s: String) = output.append(s)
 }