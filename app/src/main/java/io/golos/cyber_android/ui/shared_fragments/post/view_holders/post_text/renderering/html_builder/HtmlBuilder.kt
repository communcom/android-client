package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder

@Suppress("SpellCheckingInspection")
class HtmlBuilder {
    private val output = StringBuilder()

    fun build(): String = output.toString()

    fun clear() = output.clear()

    fun putDoctype() = putTag("!DOCTYPE html")

    fun putHtml(innerAction: () -> Unit) = putSection("html", innerAction)

    fun putStyles() =
        putSection("style") {
            putString(".border { border-style: solid; border-color: #bbbbbb; border-width: 1pt; }")
            putString(".image_description { text-align: center; font-style: italic; }")
            putString(".carousel_height { height: 120pt; }")
            putString(".carousel { width: 100%; overflow: visible; white-space:nowrap }")
            putString(".carousel_block { display: inline-block; padding-left: 2pt; }")
            putString(".carousel_block_last { display: inline-block; padding-left: 1pt; padding-right: 8pt; }")
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
                putTag("h$it")
                putString(text)
                putTag("h$it", false)

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
        putSection("figcaption", "image_description") {
            append(text)
        }

    fun putPostMap(innerAction: () -> Unit) =
        putSection("div", "carousel carousel_height") {
            innerAction()
        }

    fun putPostMapBlock(imageUrl: String) =
        putSection("div", "carousel_block") {
            putImage(imageUrl, "carousel_height", null)
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