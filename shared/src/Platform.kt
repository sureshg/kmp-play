import kotlinx.coroutines.CoroutineScope

expect class Platform() {
  fun name(): String
}

/** Runs [block] as the application's root coroutine. */
expect fun runMain(block: suspend CoroutineScope.() -> Unit)
