import config.Config
import config.configRoutes
import config.initProps
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*
import rpc.rpcRoutes

fun main(args: Array<String>) =
    try {
      initProps()
      EngineMain.main(args)
    } catch (e: Throwable) {
      val log = KtorSimpleLogger("main")
      log.error("Failed to start the app: ${e.message}", e)
    }

suspend fun Application.module() {
  log.info("Starting the app ...")
  val config = ApplicationConfig("application.conf")
  println(config.property("ktor").getMap())
  println(config.mergeWith(config).getAs<Config>())

  configRoutes()
  rpcRoutes()
}
