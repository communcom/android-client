package io.golos.posts_editor.components

import io.golos.posts_editor.components.input.InputExtensions

class ComponentsWrapper(val inputExtensions: InputExtensions?) {

    class Builder {
        private var inputExtensions: InputExtensions? = null
        private var embedExtensions: EmbedExtensions? = null

        fun inputExtensions(inputExtensions: InputExtensions): Builder {
            this.inputExtensions = inputExtensions
            return this
        }

        fun imageExtensions(embedExtensions: EmbedExtensions): Builder {
            this.embedExtensions = embedExtensions
            return this
        }

        fun build(): ComponentsWrapper = ComponentsWrapper(this.inputExtensions)
    }
}
