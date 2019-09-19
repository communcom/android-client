package io.golos.domain

import java.io.File

interface BitmapsUtils {
    /**
     * Correct image rotation direction
     */
    fun correctOrientation(file: File): File
}