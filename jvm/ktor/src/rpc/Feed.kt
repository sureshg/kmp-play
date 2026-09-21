package rpc

import dev.suresh.rpc.Feed
import kotlinx.coroutines.flow.flow

data class FeedConfig(val name: String)

class FeedImpl(val params: FeedConfig) : Feed {
  override fun data(input: String) = flow {
    emit("Hello $input")
    emit("Param: ${params.name}")
  }

  override suspend fun ping() = "Pong"
}
