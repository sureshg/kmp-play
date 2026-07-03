package dev.suresh.http

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.util.*
import kotlin.concurrent.atomics.AtomicBoolean

class CurlLoggingConfig {
  var logger: KLogger? = null
  var redactedHeaders: Set<String> = [HttpHeaders.Authorization]
  var redactedQueryParams: Set<String> = ["api_key", "token", "sig", "access_token"]
  var enabled = AtomicBoolean(true)
}

val CurlLogging =
    createClientPlugin("CurlLogging", ::CurlLoggingConfig) {
      val logger = pluginConfig.logger ?: return@createClientPlugin
      val redactedHeaders = pluginConfig.redactedHeaders.map { it.lowercase() }
      val redactedParams = pluginConfig.redactedQueryParams.map { it.lowercase() }
      val enabled = pluginConfig.enabled

      // Read-only observer in the Monitoring phase — the same place Ktor's own Logging
      // plugin sits, after the body has been rendered to an OutgoingContent.
      client.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
        val content = subject as? OutgoingContent
        if (content != null && enabled.load() && logger.isDebugEnabled()) {
          logger.debug { toCurl(context, content, redactedHeaders, redactedParams) }
        }
      }
    }

private fun toCurl(
    request: HttpRequestBuilder,
    content: OutgoingContent,
    redactedHeaders: List<String>,
    redactedParams: List<String>,
): String = buildString {
  append("curl")

  val method = request.method.value
  if (method != "GET") append(" -X $method")

  val headers = headers {
    appendAll(request.headers)
    content.contentType?.let { appendIfNameAbsent(HttpHeaders.ContentType, it.toString()) }
  }

  headers.forEach { name, values ->
    val value = if (name.lowercase() in redactedHeaders) "***" else values.joinToString(", ")
    append(" -H ").append("$name: $value".quoted())
  }

  // Body — in-memory content is inlined as -d. Streaming bodies can't be read without
  // consuming the one-shot send stream, so they get a clear placeholder instead.
  when (content) {
    is TextContent -> append(" -d ").append(content.text.quoted())
    is ByteArrayContent -> append(" -d ").append(content.bytes().decodeToString().quoted())
    is OutgoingContent.ReadChannelContent,
    is OutgoingContent.WriteChannelContent -> append(" -d '[streaming body omitted]'")
    else -> Unit // EmptyContent
  }

  // Compressed flag
  if (request.headers.contains(HttpHeaders.AcceptEncoding)) {
    append(" --compressed")
  }

  append(" ").append(redactedUrl(request.url, redactedParams).quoted())
}

/** POSIX single-quote escaping: close, emit an escaped quote, reopen. */
private fun String.quoted() = replace("'", """'\''""").let { "'$it'" }

/** Masks userinfo and secret query params without mutating the original request URL. */
private fun redactedUrl(source: URLBuilder, redactedParams: List<String>) =
    URLBuilder()
        .apply {
          takeFrom(source)
          if (user != null) user = "***"
          if (password != null) password = "***"
          parameters
              .names()
              .filter { it.lowercase() in redactedParams }
              .forEach { parameters[it] = "***" }
        }
        .buildString()
