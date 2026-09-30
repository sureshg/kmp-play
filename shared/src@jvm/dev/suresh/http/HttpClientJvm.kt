package dev.suresh.http

import certkit.pem.Pem
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.engine.java.*
import nl.altindag.ssl.SSLFactory

val rootCAs by lazy {
  val caCerts =
      Thread.currentThread()
          .contextClassLoader
          .getResource("ca/cacert.pem")
          ?.readText(Charsets.US_ASCII) ?: error("Failed to load bundled root certificates")
  Pem.readCertificateChain(caCerts).onEach { it.checkValidity() }
}

val customSSLFactory: SSLFactory by lazy {
  SSLFactory.builder()
      .withDefaultTrustMaterial()
      .withTrustMaterial(rootCAs)
      .withSwappableTrustMaterial()
      .withSslParametersEnhancer {}
      .build()
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
