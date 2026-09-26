package dev.suresh.http

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.engine.java.*
import nl.altindag.ssl.SSLFactory

val customSSLFactory: SSLFactory by lazy {
  SSLFactory.builder().withDefaultTrustMaterial().withSwappableTrustMaterial().build()
}

actual fun httpClient(
    name: String,
    timeout: Timeout,
    retry: Retry,
    httpLogger: KLogger,
    config: ClientConfig,
) =
    HttpClient(Java) {
      config(this)
      engine { config { sslContext(customSSLFactory.sslContext) } }
    }
