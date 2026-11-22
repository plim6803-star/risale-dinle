package com.benimradyom.app.data.ai

import org.json.JSONObject
import java.io.DataOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * Yapay zeka istemcilerinin ortak HTTP katmani.
 *
 * Onceden dort istemci ayni baglanti / hata / yanit ayiklama kodunu ayri ayri
 * tasiyordu ve HTTPS kontrolu yalnizca ikisinde vardi. Burada tek yerde:
 *  - her istek HTTPS olmak zorunda (anahtar asla duz metin gitmez)
 *  - baglanti hata durumunda da kapatilir
 *  - hata mesajlari ayni bicimde uretilir
 */
internal object AiHttp {

    /**
     * Ayarlardaki Responses adresinden ayni sunucunun baska bir kaynagini turetir.
     * Ornek: https://api.openai.com/v1/responses -> https://api.openai.com/v1/audio/transcriptions
     */
    fun endpoint(configuredResponsesEndpoint: String, resource: String): String {
        val configured = configuredResponsesEndpoint.trim().trimEnd('/')
        val marker = "/v1/"
        val index = configured.indexOf(marker)
        return if (index >= 0) {
            configured.substring(0, index + marker.length) + resource
        } else {
            runCatching {
                val url = URL(configured)
                "${url.protocol}://${url.authority}/v1/$resource"
            }.getOrElse { "$configured/v1/$resource" }
        }
    }

    fun requireHttps(url: String) {
        require(url.trim().startsWith("https://", ignoreCase = true)) {
            "API adresi HTTPS olmali"
        }
    }

    /** JSON gonderir, JSON yanit dondurur. [label] hata mesajinin basina yazilir. */
    fun postJson(
        url: String,
        apiKey: String,
        body: JSONObject,
        label: String,
        readTimeoutMs: Int
    ): JSONObject {
        val connection = open(url, apiKey, readTimeoutMs)
        return try {
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            readJson(connection, label)
        } finally {
            connection.disconnect()
        }
    }

    /** Coklu parca (dosya yukleme) istegi. */
    fun postMultipart(
        url: String,
        apiKey: String,
        label: String,
        readTimeoutMs: Int,
        write: MultipartWriter.() -> Unit
    ): JSONObject {
        val boundary = "BenimRadyom-${UUID.randomUUID()}"
        val connection = open(url, apiKey, readTimeoutMs)
        return try {
            connection.setChunkedStreamingMode(64 * 1024)
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            DataOutputStream(connection.outputStream).use { out ->
                MultipartWriter(out, boundary).apply(write).finish()
            }
            readJson(connection, label)
        } finally {
            connection.disconnect()
        }
    }

    /** Responses API yanitindan duz metni cikarir. */
    fun extractOutputText(root: JSONObject): String {
        root.optString("output_text").takeIf { it.isNotBlank() }?.let { return it }
        val output = root.optJSONArray("output") ?: error("API yanitinda output yok")
        for (i in 0 until output.length()) {
            val content = output.optJSONObject(i)?.optJSONArray("content") ?: continue
            for (j in 0 until content.length()) {
                val part = content.optJSONObject(j) ?: continue
                if (part.optString("type") == "output_text") {
                    part.optString("text").takeIf { it.isNotBlank() }?.let { return it }
                }
            }
        }
        error("API yanitinda metin yok")
    }

    /** Modelin bazen ekledigi ```json ... ``` kusagini temizler. */
    fun stripCodeFence(value: String): String = value.trim()
        .removePrefix("```json").removePrefix("```").removeSuffix("```").trim()

    private fun open(url: String, apiKey: String, readTimeoutMs: Int): HttpURLConnection {
        requireHttps(url)
        require(apiKey.isNotBlank()) { "API anahtari gerekli" }
        return (URL(url.trim()).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 30_000
            readTimeout = readTimeoutMs
            doOutput = true
            setRequestProperty("Authorization", "Bearer $apiKey")
        }
    }

    private fun readJson(connection: HttpURLConnection, label: String): JSONObject {
        val status = connection.responseCode
        val text = (if (status in 200..299) connection.inputStream else connection.errorStream)
            ?.bufferedReader()?.use { it.readText() }.orEmpty()
        if (status !in 200..299) {
            val message = runCatching {
                JSONObject(text).optJSONObject("error")?.optString("message")
            }.getOrNull().orEmpty()
            error("$label $status: ${message.ifBlank { text.take(180) }}")
        }
        return runCatching { JSONObject(text) }.getOrElse { error("$label: gecersiz yanit") }
    }

    class MultipartWriter internal constructor(
        private val out: DataOutputStream,
        private val boundary: String
    ) {
        fun field(name: String, value: String) {
            out.writeBytes("--$boundary\r\n")
            out.writeBytes("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
            out.write(value.toByteArray(Charsets.UTF_8))
            out.writeBytes("\r\n")
        }

        fun file(name: String, fileName: String, mime: String, input: InputStream) {
            val safeName = fileName.replace("\"", "").replace("\r", "").replace("\n", "")
            out.writeBytes("--$boundary\r\n")
            out.writeBytes("Content-Disposition: form-data; name=\"$name\"; filename=\"")
            out.write(safeName.toByteArray(Charsets.UTF_8))
            out.writeBytes("\"\r\n")
            out.writeBytes("Content-Type: $mime\r\n\r\n")
            input.copyTo(out, 64 * 1024)
            out.writeBytes("\r\n")
        }

        internal fun finish() {
            out.writeBytes("--$boundary--\r\n")
            out.flush()
        }
    }
}
