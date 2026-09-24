package config

import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlinx.serialization.Serializable

@Serializable data class Config(val host: String, val port: Int, val environment: Environment)

@Serializable
enum class Environment {
  PROD,
  DEV,
}

/**
 * Initializes the system properties required for the application to run. This should be invoked
 * before the Engine main() method is called.
 */
fun initProps() {
  val logDir =
      System.getProperty("LOG_DIR", System.getenv("LOG_DIR")).orEmpty().ifBlank {
        when {
          Path("/log").exists() -> "/log"
          else -> System.getProperty("user.dir")
        }
      }

  System.setProperty("jdk.tls.maxCertificateChainLength", "15")
  System.setProperty("jdk.includeInExceptions", "hostInfo")
  System.setProperty("slf4j.internal.verbosity", "WARN")
  System.setProperty("logback.scan.enabled", "false")
  System.setProperty("LOG_DIR", logDir)
  KotlinLoggingConfiguration.logStartupMessage = false

  println("⚡ Application started ⚡")
  println("Log Dir: $logDir")
}
