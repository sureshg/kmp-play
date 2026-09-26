package dev.suresh.http

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.engine.curl.*
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

actual fun httpClient(
    name: String,
    timeout: Timeout,
    retry: Retry,
    httpLogger: KLogger,
    config: ClientConfig,
) =
    HttpClient(Curl) {
      config(this)
      engine {
        // https://youtrack.jetbrains.com/issue/KTOR-8339
        val cacertBundle = "/etc/ssl/certs/ca-certificates.crt"
        if (Platform.osFamily == LINUX && SystemFileSystem.exists(Path(cacertBundle))) {
          caInfo = cacertBundle
          httpLogger.warn { "Setting cacertBundle to $caInfo" }
        }
        sslVerify = true
        // dispatcher = Dispatchers.IO
      }
    }
