import kotlinx.coroutines.*

actual class Platform {
  actual fun name() = "Kotlin JVM ${JVersion.get()}"
}

actual fun runMain(block: suspend CoroutineScope.() -> Unit) = runBlocking(block = block)
