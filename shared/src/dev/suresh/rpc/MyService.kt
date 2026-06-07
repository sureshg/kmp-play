package dev.suresh.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc

@Rpc
interface MyService {
  fun data(input: String): Flow<String>

  suspend fun ping(): String
}
