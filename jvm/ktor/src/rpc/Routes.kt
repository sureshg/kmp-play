package rpc

import dev.suresh.http.json
import dev.suresh.rpc.Feed
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json

fun Application.rpcRoutes() {
  install(Krpc)
  routing {
    rpc("/rpc") {
      rpcConfig { serialization { json(json) } }
      registerService<Feed> { FeedImpl(FeedConfig("Kotlin ToolChain")) }
    }
  }
}
