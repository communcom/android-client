package io.golos.posts_parsing_rendering.json_to_html.html_builder

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
            append(".text_center { text-align: center; }")
            append(".border { border-style: solid; border-color: #bbbbbb; border-width: 1pt; }")
            append(".image_description { font-style: italic; }")
            append(".carousel_height { height: 120pt; }")
            append(".carousel { width: 100%; overflow: auto; white-space:nowrap }")
            append(".carousel_padding { padding-left: 2pt; }")
            append(".carousel_block { display: inline-block; }")
            append(".block_wrapper { margin-bottom: 12pt; }")
        }

    fun putScript() =
        putSection("script") {
            append("function onPostMapItemClick(linkedBlockId) {")
            append("var linkedBlock = document.getElementById(linkedBlockId);")
            append("var linkedBlockRect = linkedBlock.getBoundingClientRect();")
            append("Android.onScrollToBlock(linkedBlockRect.top, window.innerHeight);")
            append("}")
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
                putSection("h$it", "class \"text_center\"") {
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

    fun putLink(url: String, id: Long, innerAction: () -> Unit) =
        putSection("a", "href = \"$url\" id = \"${createAnchor(id)}\"") {
            innerAction()
        }

    fun putLink(url: String, innerAction: () -> Unit) =
        putSection("a", "href = \"$url\" ") {
            innerAction()
        }


    fun putFigure(url: String, innerAction: () -> Unit) {
        putSection("figure") {
            putImage(url, "border", "100%")
            innerAction()
        }
    }

    fun putFigureCaption(text: String) =
        putSection("figcaption", "class = \"image_description text_center\"") {
            append(text)
        }

    fun putPostMap(innerAction: () -> Unit) {
        isFirstBlockInPostMap = true
        putSection("div", "class = \"carousel carousel_height\"") {
            innerAction()
        }
    }

    fun putPostMapBlock(imageUrl: String, id: Long) {
        val cssClass = if(isFirstBlockInPostMap) {
            isFirstBlockInPostMap = false
            "carousel_block"
        } else {
            "carousel_block carousel_padding"
        }

        putSection("div", "class = \"$cssClass\" onclick = \"onPostMapItemClick('${createAnchor(id)}')\"") {
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
        putSection("div", "class = \"block_wrapper\"") {
            innerAction()
        }

    fun putBlockAnchor(id: Long, innerAction: () -> Unit) =
        putSection("div", "id = \"${createAnchor(id)}\"") {
            innerAction()
        }

    private fun putSection(sectionTag: String, innerAction: () -> Unit) {
        putTag(sectionTag)
        innerAction()
        putTag(sectionTag, false)
    }

    private fun putSection(sectionTag: String, attrubutes: String,  innerAction: () -> Unit) {
        append("<")
        append(sectionTag)
        append(" ")
        append(attrubutes)
        append(" >")

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

    private fun createAnchor(id: Long) = "anchor$id"
 }