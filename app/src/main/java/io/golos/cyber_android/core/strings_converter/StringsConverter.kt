package io.golos.cyber_android.core.strings_converter

interface StringsConverter {
    fun toBytes(data: String): ByteArray

    fun toBase64(data: ByteArray): String

    fun fromBase64(data: String): ByteArray

    fun fromBytes(data: ByteArray): String
}