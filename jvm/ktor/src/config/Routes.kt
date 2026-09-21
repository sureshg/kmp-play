package config

import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configRoutes() {
  routing {
    get("/") { call.respondText("Kotlin ${KotlinVersion.CURRENT}") }

    swaggerUI(path = "docs") {
      info =
          OpenApiInfo(
              title = "Ktor App",
              version = "1.0.0",
          )
    }
  }
}
